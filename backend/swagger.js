const swaggerJsdoc = require("swagger-jsdoc");

const options = {
  definition: {
    openapi: "3.0.0",
    info: {
      title: "KerjaDekat API",
      version: "1.0.0",
      description: "API backend aplikasi KerjaDekat"
    },
    servers: [
      {
        url: "http://localhost:3000"
      }
    ],
    components: {
      schemas: {
        JobInput: {
          type: "object",
          required: [
            "job_name",
            "nama_perusahaan",
            "deskripsi_utama",
            "lokasi",
            "perkiraan_salary"
          ],
          properties: {
            job_name: {
              type: "string",
              example: "Tukang Kayu"
            },
            nama_perusahaan: {
              type: "string",
              example: "CV Maju Jaya"
            },
            deskripsi_utama: {
              type: "string",
              example: "Membutuhkan tukang kayu untuk membuat meja."
            },
            lokasi: {
              type: "string",
              example: "Padang"
            },
            perkiraan_salary: {
              type: "number",
              example: 500000
            },
            status_enum: {
              type: "string",
              enum: ["open", "in_progress", "completed"],
              example: "open"
            },
            scoring: {
              type: "number",
              example: 85.5
            }
          }
        }
      }
    }
  },
  apis: ["./routes/*.js"]
};

const swaggerSpec = swaggerJsdoc(options);

module.exports = swaggerSpec;