const axios = require("axios");

const getJobScore = async (skillText, topN = 10) => {
  try {
    const response = await axios.post(
      process.env.ML_API_URL,
      {
        skill_text: skillText,
        top_n: topN
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