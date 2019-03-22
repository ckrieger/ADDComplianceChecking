FROM node:9.4.0-alpine
COPY server.js .
COPY package.json .
COPY message-service.js .
COPY views /views
RUN npm install &&\
    apk update &&\
    apk upgrade
EXPOSE  5000
CMD node server.js ; 