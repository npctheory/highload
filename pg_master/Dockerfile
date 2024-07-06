FROM postgres:alpine3.20
RUN apk update \
    && apk add --no-cache \
        bash \
        python3 \
        py3-pip \
    && rm -rf /var/cache/apk/*

COPY pg_master/docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

COPY pg_master/001_init.sql /docker-entrypoint-initdb.d/
COPY pg_master/002_init.sql /docker-entrypoint-initdb.d/
COPY pg_master/003_create_replicator_role.sql /docker-entrypoint-initdb.d/

RUN mkdir -p /pgslave && \
    chown -R postgres:postgres /pgslave

ARG PG_NET
ENV PG_NET=${PG_NET}

EXPOSE 5432
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["postgres"]
