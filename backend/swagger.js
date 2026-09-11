const swaggerJSDoc = require("swagger-jsdoc");

const options = {
  definition: {
    openapi: "3.0.0",
    info: {
      title: "KerjaDekat API",
      version: "1.0.0",
      description: "API untuk platform KerjaDekat"
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
              example: "Backend Developer"
            },
            nama_perusahaan: {
              type: "string",
              example: "PT Contoh Indonesia"
            },
            deskripsi_utama: {
              type: "string",
              example: "Mengembangkan REST API"
            },
            lokasi: {
              type: "string",
              example: "Padang"
            },
            perkiraan_salary: {
              type: "number",
              example: 7000000
            },
            status_enum: {
              type: "string",
              enum: [
                "open",
                "in_progress",
                "completed"
              ],
              example: "open"
            },
            scoring: {
              type: "number",
              example: 85
            }
          }
        }
      }
    }
  },

  apis: [
    "./routes/*.js"
  ]
};

const swaggerSpec = swaggerJSDoc(options);

module.exports = swaggerSpec;