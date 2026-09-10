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