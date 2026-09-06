const express = require("express");
const Job = require("../models/Job");

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
    const jobs = await Job.getAll();

    res.status(200).json(jobs);
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
router.post("/", async (req, res) => {
  try {
    const job = await Job.create(req.body);

    res.status(201).json(job);
  } catch (error) {
    console.error(error);

    res.status(400).json({
      message: "Gagal membuat job",
      error: error.message
    });
  }
});

module.exports = router;