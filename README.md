## Как запустить

1. **Создать Docker-сеть**
```bash
docker network create pg_net
```
2. **Собрать Docker-имиджи серверов Postgres**
```bash
docker build -t pg_master --build-arg PG_NET=$(docker network inspect pg_net -f '{{range .IPAM.Config}}{{.Subnet}}{{end}}') -f pg_master/Dockerfile . &&
```
3. **Запустить Docker Compose**
```
docker compose up
```
4. **Проверка**
```bash
docker exec -it pg_master //bin//bash
cat /var/lib/postgresql/data/pg_hba.conf
grep -E '^(ssl|wal_level|max_wal_senders)\s*=' /var/lib/postgresql/data/postgresql.conf || echo "Parameters not found."

docker exec -it pg_master su - postgres -c psql
SELECT rolname FROM pg_roles WHERE rolname = 'replicator';

pg_basebackup -h pg_master -D /pgslave -U replicator -v -P --wal-method=stream
docker cp pg_master:/pgslave volumes/pgslave/
```

**Запустить мастер отдельно**
```
docker run -d --name pg_master --restart unless-stopped -e POSTGRES_DB=user -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 --network=pg_net pg_master
```

## Как это работает
