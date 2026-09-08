const { Pool } = require("pg");

const pool = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
<<<<<<< HEAD
  database: process.env.POSTGRES_DB,
  user: process.env.POSTGRES_USER,
  password: process.env.POSTGRES_PASSWORD
=======
  database: process.env.POSTGRES_DB || process.env.DB_NAME,
  user: process.env.POSTGRES_USER || process.env.DB_USER,
  password: process.env.POSTGRES_PASSWORD || process.env.DB_PASSWORD
>>>>>>> 856d559 (update backend upload pagination swagger)
});

const connectDB = async () => {
  try {
    const client = await pool.connect();
    console.log("PostgreSQL connected");
    client.release();
  } catch (error) {
    console.error("PostgreSQL connection failed:", error.message);
  }
};

<<<<<<< HEAD
module.exports = {
  pool,
  connectDB
};
=======
module.exports = pool;
module.exports.connectDB = connectDB;
>>>>>>> 856d559 (update backend upload pagination swagger)
