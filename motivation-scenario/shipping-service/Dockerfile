FROM node:9.4.0-alpine
COPY server.js .
COPY package.json .
COPY message-service.js .
RUN npm install &&\
    apk update &&\
    apk upgrade
EXPOSE  8088
CMD node server.js