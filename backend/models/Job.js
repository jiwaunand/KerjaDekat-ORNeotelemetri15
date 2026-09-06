const mongoose = require("mongoose");

const jobSchema = new mongoose.Schema(
  {
    job_name: {
      type: String,
      required: true
    },

    nama_perusahaan: {
      type: String,
      required: true
    },

    deskripsi_utama: {
      type: String,
      required: true
    },

    lokasi: {
      type: String,
      required: true
    },

    perkiraan_salary: {
      type: Number,
      required: true
    },

    status_enum: {
      type: String,
      enum: ["open", "in_progress", "completed"],
      default: "open"
    },

    scoring: {
      type: Number,
      default: 0
    }
  },
  {
    timestamps: true
  }
);

module.exports = mongoose.model("Job", jobSchema);