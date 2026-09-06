require("dotenv").config();

const express = require("express");
const connectDB = require("./config/db");

const jobsRouter = require("./routes/jobsRoutes");

const swaggerUi = require("swagger-ui-express");
const swaggerSpec = require("./swagger");

const app = express();

const PORT = process.env.PORT || 3000;

// Middleware
app.use(express.json());

// Connect MongoDB
connectDB();

// Health check
app.get("/health", (req, res) => {
  res.json({
    status: "OK"
  });
});

// Jobs
app.use("/jobs", jobsRouter);

// Swagger
app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));

app.listen(PORT, () => {
  console.log(`Server berjalan di http://localhost:${PORT}`);
  console.log(`Swagger: http://localhost:${PORT}/api-docs`);
});