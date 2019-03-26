FROM node:9.4.0-alpine
COPY server.js .
COPY message-service.js .
COPY package.json .
RUN npm install &&\
    apk update &&\
    apk upgrade
EXPOSE  5050
CMD node server.js ; 