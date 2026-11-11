#FROM ubuntu:latest
FROM aarch64/ubuntu:latest
#FROM node:latest

USER root
RUN apt-get update && apt-get install -y apt-transport-https
RUN apt -y install curl
RUN curl -sL https://deb.nodesource.com/setup_17.x -o nodesource_setup.sh 
RUN bash nodesource_setup.sh && apt install nodejs -y

ENV NODE_ENV=development
WORKDIR /API
COPY package*.json ./
RUN npm install --silent
COPY . .
RUN npm run build
EXPOSE 4000
ENTRYPOINT ["npm", "run", "start"]
