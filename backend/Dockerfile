FROM python:3.10.5-alpine3.15

RUN pip install fastapi uvicorn environs psycopg2-binary sqlalchemy pytest requests
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY ./start.sh /start.sh
RUN chmod +x /start.sh

CMD ["/bin/sh", "-c", "/start.sh"]

