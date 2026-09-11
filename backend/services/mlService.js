const axios = require("axios");

const getJobScore = async (skillText) => {
  try {
    // ML belum tersedia
    if (!process.env.ML_API_URL) {
      console.log("ML_API_URL belum tersedia, scoring dilewati.");
      return null;
    }

    const response = await axios.post(
      process.env.ML_API_URL,
      {
        skill_text: skillText
      }
    );

    return String(response.data);

  } catch (error) {
    console.error("ML request failed:", error.message);
    throw error;
  }
};

module.exports = {
  getJobScore
};