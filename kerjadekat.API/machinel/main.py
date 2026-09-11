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
# 2. SCHEMA INPUT
# ---------------------------------------------------------------------------
class WorkerInput(BaseModel):
    skill_text: str
    top_n: int = 10


class JobInput(BaseModel):
    judul: str
    deskripsi: str
    top_n: int = 10


# ---------------------------------------------------------------------------
# 3. ENDPOINT REKOMENDASI LOWONGAN UNTUK PEKERJA (Worker -> Jobs)
# ---------------------------------------------------------------------------
@app.post("/recommend/jobs")
def recommend_jobs_for_worker(payload: WorkerInput):
    if not payload.skill_text.strip():
        raise HTTPException(
            status_code=400, detail="Teks skill tidak boleh kosong"
        )

    teks_bersih = clean_text(payload.skill_text)
    vector_baru = vectorizer.transform([teks_bersih])
    scores = cosine_similarity(vector_baru, job_vectors).flatten()

    result = jobs_df.copy()
    result["skill_score"] = (scores * 100).round(2)

    top_jobs = result.sort_values("skill_score", ascending=False).head(
        payload.top_n
    )

    cols = [
        "job_id",
        "judul",
        "company",
        "sumber",
        "kategori",
        "deskripsi",
        "skill_score",
    ]

    return {
        "status": "success",
        "total_results": len(top_jobs),
        "data": top_jobs[cols].to_dict(orient="records"),
    }


# ---------------------------------------------------------------------------
# 4. ENDPOINT REKOMENDASI PEKERJA UNTUK LOWONGAN BARU (Job -> Workers)
# ---------------------------------------------------------------------------
@app.post("/recommend/workers")
def recommend_workers_for_job(payload: JobInput):
    teks_gabung = payload.judul + " " + payload.deskripsi
    if not teks_gabung.strip():
        raise HTTPException(
            status_code=400, detail="Judul atau deskripsi tidak boleh kosong"
        )

    teks_bersih = clean_text(teks_gabung)
    vector_baru = vectorizer.transform([teks_bersih])
    scores = cosine_similarity(vector_baru, worker_vectors).flatten()

    result = workers_df.copy()
    result["skill_score"] = (scores * 100).round(2)

    top_workers = result.sort_values("skill_score", ascending=False).head(
        payload.top_n
    )

    cols = ["worker_id", "nama", "jenis_kelamin", "skill_text", "skill_score"]

    return {
        "status": "success",
        "total_results": len(top_workers),
        "data": top_workers[cols].to_dict(orient="records"),
    }


# ---------------------------------------------------------------------------
# 5. ENDPOINT MARKET TREND ANALYTICS (Analisis Tren Pasar)
# ---------------------------------------------------------------------------
@app.get("/analytics/top-skills")
def get_top_market_skills(top_n: int = 10):
    all_text = " ".join(jobs_df["deskripsi"].fillna("").astype(str).tolist())
    words = re.findall(r"\b[a-zA-Z]{3,}\b", all_text.lower())

    # Stopwords domain lowongan kerja untuk menyaring kata-kata template
    stopwords_domain = {
        "yang",
        "untuk",
        "dan",
        "atau",
        "dengan",
        "ini",
        "itu",
        "adalah",
        "akan",
        "bisa",
        "dapat",
        "para",
        "dalam",
        "sebagai",
        "oleh",
        "sudah",
        "telah",
        "juga",
        "tidak",
        "agar",
        "karena",
        "jika",
        "maka",
        "lebih",
        "kami",
        "kita",
        "anda",
        "dia",
        "mereka",
        "saya",
        "kamu",
        "serta",
        "membutuhkan",
        "dibutuhkan",
        "mencari",
        "dicari",
        "butuh",
        "cari",
        "kandidat",
        "pengalaman",
        "minimal",
        "tahun",
        "bulan",
        "hari",
        "kerja",
        "gaji",
        "upah",
        "bergabung",
        "tim",
        "perusahaan",
        "persyaratan",
        "deskripsi",
        "pekerjaan",
        "requirement",
        "memiliki",
        "sederajat",
        "pendidikan",
        "baik",
        "melakukan",
        "usia",
        "diploma",
        "staff",
        "and",
        "the",
        "with",
        "able",
        "good",
        "responsible",
        "bekerja",
        "slta",
        "sma",
        "smk",
        "mampu",
        "membuat",
        "pria",
        "wanita",
        "waktu",
        "sarjana",
        "penuh",
        "part",
        "full",
        "time",
        "jam",
        "lokasi",
        "domisili",
        "wajib",
        "diutamakan",
        "menguasai",
        "kemampuan",
        "jujur",
        "target",
        "jawab",
        "max",
        "maksimal",
        "bidang",
        "manager",
        "lulusan",
        "jurusan",
        "posisi",
        "sistem",
        "experience",
        "work",
        "team",
        "communication",
        "management",
        "business",
        "service",
        "skills",
        "skill",
        "tingkat",
        "tinggi",
        "terkait",
        "standar",
        "umum",
        "ketentuan",
        "bersedia",
        "jakarta",
        "sesuai",
        "laki",
        "komunikasi",
        "laporan",
        "tempat",
        "area",
        "wilayah",
        "cabang",
        "kota",
        "indonesia",
    }

    filtered_words = [w for w in words if w not in stopwords_domain]
    top_skills = Counter(filtered_words).most_common(top_n)

    result = [
        {"skill": skill, "frequency": count} for skill, count in top_skills
    ]
    return {"status": "success", "total_data": len(result), "data": result}