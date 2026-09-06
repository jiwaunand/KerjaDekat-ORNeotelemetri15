const { pool } = require("../config/db");

const Job = {
  async getAll() {
    const result = await pool.query(
      "SELECT * FROM jobs ORDER BY id DESC"
    );

    return result.rows;
  },

  async create(data) {
    const {
      job_name,
      nama_perusahaan,
      deskripsi_utama,
      lokasi,
      perkiraan_salary
    } = data;

    const result = await pool.query(
      `INSERT INTO jobs
      (job_name, nama_perusahaan, deskripsi_utama, lokasi, perkiraan_salary)
      VALUES ($1, $2, $3, $4, $5)
      RETURNING *`,
      [
        job_name,
        nama_perusahaan,
        deskripsi_utama,
        lokasi,
        perkiraan_salary
      ]
    );

    return result.rows[0];
  }
};

module.exports = Job;