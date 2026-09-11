const express = require("express");
const Job = require("../models/Job");
const { createUploadUrl } = require("../services/r2Service");
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

    const result = await Job.getAll(page, limit);

    res.status(200).json({
      data: result.jobs,
      pagination: {
        page: page,
        limit: limit,
        hasNextPage: result.hasNextPage
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
 *             type: object
 *             required:
 *               - job_name
 *               - nama_perusahaan
 *               - deskripsi_utama
 *               - lokasi
 *               - perkiraan_salary
 *             properties:
 *               job_name:
 *                 type: string
 *                 example: Backend Developer
 *               nama_perusahaan:
 *                 type: string
 *                 example: PT Contoh Indonesia
 *               deskripsi_utama:
 *                 type: string
 *                 example: Mengembangkan REST API menggunakan Node.js
 *               lokasi:
 *                 type: string
 *                 example: Padang
 *               perkiraan_salary:
 *                 type: integer
 *                 example: 5000000
 *               image_url:
 *                 type: string
 *                 example: https://kerjadekatproject.9414295e210f876f77e8f1ace4c716b8.r2.cloudflarestorage.com/1789133983321-perusahaan.jpg
 *     responses:
 *       201:
 *         description: Pekerjaan berhasil ditambahkan
 *       400:
 *         description: Data tidak valid
 *       500:
 *         description: Gagal menambahkan pekerjaan
 */
router.post("/", async (req, res) => {
  try {
    const {
      job_name,
      nama_perusahaan,
      deskripsi_utama,
      lokasi,
      perkiraan_salary,
      image_url
    } = req.body;

    if (
      !job_name ||
      !nama_perusahaan ||
      !deskripsi_utama ||
      !lokasi ||
      !perkiraan_salary
    ) {
      return res.status(400).json({
        message: "Data pekerjaan wajib diisi"
      });
    }

    const job = await Job.create({
      job_name,
      nama_perusahaan,
      deskripsi_utama,
      lokasi,
      perkiraan_salary,
      image_url: image_url || null
    });

    res.status(201).json({
      message: "Pekerjaan berhasil ditambahkan",
      data: job
    });

  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: "Gagal menambahkan pekerjaan",
      error: error.message
    });
  }
});

/**
 * @swagger
 * /jobs/score:
 *   post:
 *     summary: Mengirim permintaan scoring pekerjaan ke ML
 *     tags: [Jobs]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - skill_text
 *             properties:
 *               skill_text:
 *                 type: string
 *                 example: "Python, SQL, REST API"
 *               top_n:
 *                 type: integer
 *                 example: 10
 *     responses:
 *       200:
 *         description: Hasil scoring dari ML
 *       400:
 *         description: Data tidak valid
 *       500:
 *         description: Gagal menghubungi ML
 */
router.post("/score", async (req, res) => {
  try {
    const { skill_text, top_n } = req.body;

    if (!skill_text) {
      return res.status(400).json({
        message: "skill_text wajib diisi"
      });
    }

    const result = await getJobScore(
      skill_text,
      top_n || 10
    );

    res.status(200).json(result);

  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: "Gagal menghubungi ML",
      error: error.message
    });
  }
});

/**
 * @swagger
 * /jobs/upload-url:
 *   post:
 *     summary: Membuat presigned URL untuk upload gambar pekerjaan
 *     tags: [Jobs]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - file_name
 *               - content_type
 *             properties:
 *               file_name:
 *                 type: string
 *                 example: perusahaan.jpg
 *               content_type:
 *                 type: string
 *                 example: image/jpeg
 *     responses:
 *       200:
 *         description: Presigned URL berhasil dibuat
 *       400:
 *         description: Data upload tidak valid
 *       500:
 *         description: Gagal membuat presigned URL
 */
router.post("/upload-url", async (req, res) => {
  try {
    const { file_name, content_type } = req.body;

    if (!file_name || !content_type) {
      return res.status(400).json({
        message: "file_name dan content_type wajib diisi"
      });
    }

    if (!content_type.startsWith("image/")) {
      return res.status(400).json({
        message: "File harus berupa gambar"
      });
    }

    const result = await createUploadUrl(
      file_name,
      content_type
    );

    res.status(200).json({
      message: "Presigned URL berhasil dibuat",
      upload_url: result.uploadUrl,
      key: result.key
    });

  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: "Gagal membuat presigned URL",
      error: error.message
    });
  }
});

/**
 * @swagger
 * /jobs/{id}:
 *   get:
 *     summary: Mendapatkan detail pekerjaan berdasarkan ID
 *     tags: [Jobs]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *         description: ID pekerjaan
 *     responses:
 *       200:
 *         description: Detail pekerjaan
 *       404:
 *         description: Job tidak ditemukan
 *       500:
 *         description: Gagal mengambil detail job
 */
router.get("/:id", async (req, res) => {
  try {
    const id = parseInt(req.params.id);

    const job = await Job.getById(id);

    if (!job) {
      return res.status(404).json({
        message: "Job tidak ditemukan"
      });
    }

    res.status(200).json(job);
  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: "Gagal mengambil detail job",
      error: error.message
    });
  }
});

module.exports = router;