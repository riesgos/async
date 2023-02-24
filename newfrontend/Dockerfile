# Stage 1: building
FROM node:16.18.0-buster-slim as build

RUN mkdir -p /mybuilddir
WORKDIR /mybuilddir

COPY . .

# doing npm ci early in procress so that it can be cached easily.
RUN npm ci


ARG FRONTEND_PATH
ARG FILESTORAGE_URL
ARG QUEUE_URL
ARG FASTAPI_URL
ARG LOGS_URL
RUN test -n "$FRONTEND_PATH"
RUN test -n "$FILESTORAGE_URL"
RUN test -n "$QUEUE_URL"
RUN test -n "$FASTAPI_URL"
RUN test -n "$LOGS_URL"

# sed: attempting to avoid slashes and quotation-marks ... lead to escape-hell. 
# using alternative delimiter | so that ARG's may contain slashes.
# in angular.json, replace `"baseHref": "/frontend/"` with:
RUN sed -i "s|FRONTEND_PATH_PLACEHOLDER|${FRONTEND_PATH}|" ./angular.json
# replace app-config
RUN sed -i "s|FILESTORAGE_URL_PLACEHOLDER|${FILESTORAGE_URL}|" ./src/environments/environment.prod.ts
RUN sed -i "s|QUEUE_URL_PLACEHOLDER|${QUEUE_URL}|" ./src/environments/environment.prod.ts
RUN sed -i "s|FASTAPI_URL_PLACEHOLDER|${FASTAPI_URL}|" ./src/environments/environment.prod.ts
RUN sed -i "s|LOGS_URL_PLACEHOLDER|${LOGS_URL}|" ./src/environments/environment.prod.ts

# build with `behindProxy` so that we make use of the recently substituted FRONTEND_PATH_PLACEHOLDER from angular.json
# build with `production` so that we fetch config.js, not config.dev.js, with the recently substituted FILESTORAGE_URL_PLACEHOLDER
RUN npx ng build --configuration=production,behindProxy



# Stage 2: serving
FROM nginx:latest

# Need to re-declare here because args are scoped to stage
ARG FRONTEND_PATH 

COPY --from=build /mybuilddir/dist/newfrontend /usr/share/nginx/html/${FRONTEND_PATH}/

EXPOSE 80