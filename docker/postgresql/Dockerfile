FROM postgres:15.2

ENV POSTGRES_USER="postgres"
ENV POSTGRES_PASSWORD="postgres"
ENV POSTGRES_INITDB_ARGS="--encoding=UTF-8 --locale=C"
ENV TZ: "Asia/Tokyo"

COPY restore.sql /docker-entrypoint-initdb.d/