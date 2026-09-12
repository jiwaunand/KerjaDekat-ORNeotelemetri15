from collections import Counter
import re
import joblib
from fastapi import FastAPI, HTTPException
import numpy as np
import pandas as pd
from pydantic import BaseModel
from sklearn.metrics.pairwise import cosine_similarity

app = FastAPI(
    title="KerjaDekat API",
    description="API Engine Rekomendasi & Analisis Tren Pasar",
)

# ---------------------------------------------------------------------------
# 1. LOAD 5 ARTIFACTS TERBARU DARI COLAB
# ---------------------------------------------------------------------------
vectorizer = joblib.load("artifacts/tfidf_vectorizer.pkl")
jobs_df = joblib.load("artifacts/jobs_data.pkl")
job_vectors = joblib.load("artifacts/job_vectors.pkl")
workers_df = joblib.load("artifacts/workers_data.pkl")
worker_vectors = joblib.load("artifacts/worker_vectors.pkl")


# Preprocessing teks sederhana untuk query input baru
def clean_text(text):
    if not isinstance(text, str):
        return ""
    text = text.lower()
    text = re.sub(r"[^a-z0-9\s]", " ", text)
    return re.sub(r"\s+", " ", text).strip()


# ---------------------------------------------------------------------------
# 5. ENDPOINT MARKET TREND ANALYTICS (Analisis Tren Pasar)
# ---------------------------------------------------------------------------
# ---------------------------------------------------------------------------
# 5. ENDPOINT SKOR KESESUAIAN PASAR
# ---------------------------------------------------------------------------
class JobDescriptionRequest(BaseModel):
    job_description: str


def _mean_top_similarities(similarities, top_n=10):
    """Mengambil rata-rata kemiripan tertinggi secara aman."""
    values = np.asarray(similarities, dtype=float).ravel()
    if values.size == 0:
        return 0.0

    top_n = min(top_n, values.size)
    top_values = np.partition(values, -top_n)[-top_n:]
    return float(np.mean(top_values))


@app.post("/skors")
def calculate_job_score(payload: JobDescriptionRequest):
    cleaned_description = clean_text(payload.job_description)

    if not cleaned_description:
        raise HTTPException(
            status_code=422,
            detail="job_description wajib berupa string dan tidak boleh kosong.",
        )

    query_vector = vectorizer.transform([cleaned_description])

    if query_vector.nnz == 0:
        return {
            "status": "success",
            "score": 0,
            "category": "Data tidak cukup",
            "details": {
                "job_demand_score": 0.0,
                "worker_scarcity_score": 0.0,
            },
            "message": "Tidak ada istilah pada deskripsi yang dikenali oleh model.",
        }

    job_similarities = cosine_similarity(query_vector, job_vectors).ravel()
    worker_similarities = cosine_similarity(query_vector, worker_vectors).ravel()

    # Permintaan pasar diwakili oleh kemiripan dengan listing lowongan aktif.
    job_demand_score = _mean_top_similarities(job_similarities) * 100

    # Semakin banyak profil pekerja serupa, semakin rendah skor kelangkaannya.
    worker_supply_score = _mean_top_similarities(worker_similarities) * 100
    worker_scarcity_score = 100 - worker_supply_score

    # Listing aktif menjadi faktor utama, sedangkan kelangkaan pekerja menjadi
    # faktor pendukung. Hasil akhir selalu dibatasi pada rentang 0-100.
    final_score = (0.75 * job_demand_score) + (0.25 * worker_scarcity_score)
    final_score = round(float(np.clip(final_score, 0, 100)), 2)

    if final_score >= 85:
        category = "Peluang sangat tinggi"
    elif final_score >= 70:
        category = "Peluang tinggi"
    elif final_score >= 55:
        category = "Peluang sedang"
    elif final_score >= 40:
        category = "Peluang rendah"
    else:
        category = "Peluang sangat rendah"

    return {
        "score": final_score,
    }
