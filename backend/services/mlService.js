const axios = require("axios");

const getJobScore = async (deskripsi) => {
  try {
    const response = await axios.post(
      process.env.ML_API_URL,
      {
        deskripsi: deskripsi
      }
    );

    return response.data;
  } catch (error) {
    console.error("ML request failed:", error.message);
    throw error;
  }
};

module.exports = {
  getJobScore
};