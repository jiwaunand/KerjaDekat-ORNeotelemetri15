const { S3Client, PutObjectCommand } = require("@aws-sdk/client-s3");
const { getSignedUrl } = require("@aws-sdk/s3-request-presigner");

const s3 = new S3Client({
  region: "auto",
  endpoint: `https://${process.env.R2_ACCOUNT_ID}.r2.cloudflarestorage.com`,
  credentials: {
    accessKeyId: process.env.R2_ACCESS_KEY_ID,
    secretAccessKey: process.env.R2_SECRET_ACCESS_KEY
  }
});

async function createUploadUrl(fileName, contentType) {
  const key = `${Date.now()}-${fileName}`;

  const command = new PutObjectCommand({
    Bucket: process.env.R2_BUCKET_NAME,
    Key: key,
    ContentType: contentType
  });

  const uploadUrl = await getSignedUrl(s3, command, {
    expiresIn: 300
  });

  return {
    uploadUrl,
    key
  };
}

module.exports = {
  createUploadUrl
};