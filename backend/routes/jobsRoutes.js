const express = require("express");
const Job = require("../models/Job");
const upload = require("../middleware/upload");
const { getJobScore } = require("../services/mlService");

const router = express.Router();

/**
 * @swagger
 * /jobs:
 *   get:
 *     summary: Mendapatkan semua data pekerjaan
 *     tags: [Jobs]
 *     responses:
 *       200:
 *         description: Daftar pekerjaan
 *       500:
 *         description: Gagal mengambil data jobs
 */
router.get("/", async (req, res) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;

    const jobs = await Job.getAll(page, limit);

    res.status(200).json({
      data: jobs,
      pagination: {
        page: page,
        limit: limit
      }
    });

  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: "Gagal mengambil data jobs",
      error: error.message
    });
  }
});

/**
 * @swagger
 * /jobs:
 *   post:
 *     summary: Menambahkan pekerjaan baru
 *     tags: [Jobs]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/JobInput'
 *     responses:
 *       201:
 *         description: Job berhasil dibuat
 *       400:
 *         description: Data tidak valid
 */
router.post("/", upload.single("image"), async (req, res) => {
  try {
    console.log("BODY:", req.body);
    console.log("FILE:", req.file);

    const image_url = req.file
      ? `/uploads/${req.file.filename}`
      : null;

    let mlResult = null;

    // Kirim deskripsi ke ML jika URL ML sudah tersedia
    if (process.env.ML_API_URL) {
      mlResult = await getJobScore(req.body.deskripsi_utama);

      console.log("HASIL ML:", mlResult);
    }

    const job = await Job.create({
      ...req.body,
      image_url
    });

    res.status(201).json({
      job,
      ml_result: mlResult
    });

  } catch (error) {
    console.error("POST JOB ERROR:", error);

    res.status(400).json({
      message: "Gagal membuat job",
      error: error.message
    });
  }
});

module.exports = router;