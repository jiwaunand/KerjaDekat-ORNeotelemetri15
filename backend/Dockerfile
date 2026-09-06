FROM docker.io/oven/bun:canary-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN bun install --production
COPY . .

FROM oven/bun:canary-alpine AS runner
USER bun
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder --chown=bun:bun /app/node_modules ./node_modules
COPY --from=builder --chown=bun:bun /app/package.json ./
COPY --from=builder --chown=bun:bun /app/server.js ./
EXPOSE 3000
ENTRYPOINT [ "bun", "run", "server.js" ]
