FROM node:21.5.0-bullseye-slim

WORKDIR /api
COPY package*.json ./
RUN npm config set update-notifier false

RUN if [ $(uname -m) = "aarch64" ]; then apt-get update -y && apt-get install -y build-essential libcairo2-dev libpango1.0-dev libjpeg-dev; fi

RUN npm install --silent

COPY . .
RUN npm run build
EXPOSE 4000

RUN rm -fr tsconfig.json main.ts ws routes lib database config

ENTRYPOINT ["node", "."]
