const crypto = require("crypto");
const path = require("path");

const generateFileName = (originalName) => {
  const ext = path.extname(originalName);
  const uniqueName = `${crypto.randomUUID()}${ext}`;

  return uniqueName;
};

module.exports = generateFileName;