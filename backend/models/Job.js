const pool = require("../config/db");

const Job = {
  async getAll(page = 1, limit = 10) {
    const offset = (page - 1) * limit;

    const result = await pool.query(
      `SELECT * FROM jobs
       ORDER BY id DESC
       LIMIT $1 OFFSET $2`,
      [limit, offset]
    );

    return result.rows;
  },

  async create(data) {
    const {
      job_name,
      nama_perusahaan,
      deskripsi_utama,
      lokasi,
      perkiraan_salary,
      image_url
    } = data;

    const result = await pool.query(
      `INSERT INTO jobs
      (
        job_name,
        nama_perusahaan,
        deskripsi_utama,
        lokasi,
        perkiraan_salary,
        image_url
      )
      VALUES ($1, $2, $3, $4, $5, $6)
      RETURNING *`,
      [
        job_name,
        nama_perusahaan,
        deskripsi_utama,
        lokasi,
        perkiraan_salary,
        image_url
      ]
    );

    return result.rows[0];
  }
};

module.exports = Job;