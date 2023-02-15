# Stage 1: building
FROM node:16.18.0-buster-slim as build

RUN mkdir -p /mybuilddir
WORKDIR /mybuilddir

ARG FRONTEND_URL
ARG FILESTORAGE_URL

COPY .:.

# in angular.json, replace `"baseHref": "/frontend/"` with 
RUN sed -i "s/FRONTEND_URL_PLACEHOLDER/${FRONTEND_URL}/" ./angular.json
# in ./src/assets/config.json, insert frontend-url and filestorage-url
RUN sed -i "s/FILESTORAGE_URL_PLACEHOLDER/${FILESTORAGE_URL}/" ./src/assets/config.prod.json

RUN npm ci
# build with `behindProxy` so that we make use of the recently substituted FRONTEND_URL_PLACEHOLDER from angular.json
# build with `production` so that we fetch config.js, not config.dev.js, with the recently substituted FILESTORAGE_URL_PLACEHOLDER
RUN npx ng build --configuration=production,behindProxy



# Stage 2: serving
FROM nginx:latest

COPY --from=build /mybuilddir/dist/sample-angular-app /usr/share/nginx/html

EXPOSE 80