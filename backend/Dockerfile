FROM python:3.10.5-alpine3.15

# Making sure wheel compilation works
RUN apk update
RUN apk add python3-dev gcc libc-dev g++
RUN /usr/local/bin/python -m pip install --upgrade pip


RUN pip install fastapi uvicorn environs psycopg2-binary sqlalchemy pytest requests
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

CMD ["./start.sh"]

