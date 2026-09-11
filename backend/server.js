require("dotenv").config();

const express = require("express");
const cors = require("cors");
const { connectDB } = require("./config/db");
const jobRoutes = require("./routes/jobsRoutes");
const swaggerUi = require("swagger-ui-express");
const swaggerSpec = require("./swagger");

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Static files
app.use("/uploads", express.static("uploads"));

// Swagger
app.use(
  "/api-docs",
  swaggerUi.serve,
  swaggerUi.setup(swaggerSpec)
);

// Routes
app.use("/jobs", jobRoutes);

// Root
app.get("/", (req, res) => {
  res.json({
    message: "API KerjaDekat berjalan"
  });
});

// Health check
app.get("/health", (req, res) => {
  res.json({
    status: "OK",
    message: "API KerjaDekat sehat"
  });
});

// Database
connectDB();

// Start server
app.listen(PORT, () => {
  console.log(`Server berjalan di http://localhost:${PORT}`);
});