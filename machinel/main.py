from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sklearn.metrics.pairwise import cosine_similarity
import joblib
import pandas as pd

app = FastAPI(
    title="KerjaDekat Recommendation API",
    description="API Sistem Rekomendasi KerjaDekat berbasis Content-Based Filtering (TF-IDF)",
    version="1.0.0"
)

# Load artifacts saat API dinyalakan
try:
    vectorizer = joblib.load("artifacts/tfidf_vectorizer.pkl")
    jobs_df = joblib.load("artifacts/jobs_data.pkl")
    job_vectors = joblib.load("artifacts/job_vectors.pkl")
    print("Artifacts berhasil di-load")
except Exception as e:
    print(f"Gagal me-load artifacts: {e}")

class WorkerSkillInput(BaseModel):
    skill_text: str
    top_n: int = 10

@app.get("/")
def root():
    return {"message": "KerjaDekat Recommendation API is running!"}

@app.post("/recommend")
def get_recommendations(payload: WorkerSkillInput):
    if not payload.skill_text.strip():
        raise HTTPException(status_code=400, detail="Teks skill tidak boleh kosong")

    # 1. Transform teks skill worker ke bentuk vektor TF-IDF
    worker_vec = vectorizer.transform([payload.skill_text])

    # 2. Hitung Cosine Similarity ke semua lowongan
    scores = cosine_similarity(worker_vec, job_vectors).flatten()

    # 3. Urutkan & ambil Top-N murni
    result = jobs_df.copy()
    result["skill_score"] = scores.round(3)
    top_jobs = result.sort_values("skill_score", ascending=False).head(payload.top_n)

    # 4. Return dalam format JSON
    return {
        "status": "success",
        "total_results": len(top_jobs),
        "data": top_jobs[["job_id", "judul", "sumber", "kategori", "skill_score"]].to_dict(orient="records")
    }