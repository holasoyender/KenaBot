FROM arm64v8/node:18

USER root

WORKDIR /
COPY package*.json ./
RUN npm install --silent
RUN npm install pm2 -g
COPY . .
RUN npm run build
EXPOSE 4444 4420 4460 4480 3333

ENV PM2_PUBLIC_KEY 72r6dw5m0p8horm
ENV PM2_SECRET_KEY xzhhbdaedofwqfc

#ENTRYPOINT ["npm", "run", "start"]
CMD ["pm2-runtime", "dist/main.js"]