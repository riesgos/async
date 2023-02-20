from nginx:1.23.2-alpine

RUN apk update 
RUN apk add apache2-utils

ARG USERNAME
ARG PASSWORD
RUN test -n "$USERNAME"
RUN test -n "$PASSWORD"

COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
COPY ./nginx/nginx.conf /etc/nginx/nginx.conf
RUN htpasswd -c -b /etc/nginx/.htpasswd $USERNAME $PASSWORD

EXPOSE 80

# Startup nginx with `daemon off;` so that it doesn't immediately stop again.
CMD ["nginx", "-g", "daemon off;"]