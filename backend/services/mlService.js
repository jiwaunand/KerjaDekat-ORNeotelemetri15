const axios = require("axios");

const getJobScore = async (skillText) => {
  try {
    if (!process.env.ML_API_URL) {
      console.log("ML_API_URL belum tersedia, scoring dilewati.");
      return null;
    }

    const response = await axios.post(
      process.env.ML_API_URL,
      {
        job_description: skillText
      }
    );

    console.log("Response ML:", response.data);

    // Kalau response ML berupa string
    if (typeof response.data === "string") {
      return response.data;
    }

    // Kalau response ML berupa object dan punya field score
    if (response.data && response.data.score !== undefined) {
      return String(response.data.score);
    }

    return String(response.data);

  } catch (error) {
    console.error("ML request failed:", error.message);
    throw error;
  }
};

module.exports = {
  getJobScore
};
