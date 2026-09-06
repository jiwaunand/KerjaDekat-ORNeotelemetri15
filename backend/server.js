require("dotenv").config();

const express = require("express");
const { connectDB } = require("./config/db");
const jobRoutes = require("./routes/jobsRoutes");

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

connectDB();

app.get("/", (req, res) => {
  res.json({
    message: "API KerjaDekat berjalan"
  });
});

app.use("/jobs", jobRoutes);

app.get("/health", (req, res) => {
  res.json({
    status: "OK",
    message: "API KerjaDekat sehat"
  });
});

app.listen(PORT, () => {
  console.log(`Server berjalan di http://localhost:${PORT}`);
});