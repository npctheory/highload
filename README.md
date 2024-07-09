  
## Физическая Репликация
Создаем сеть
```bash
docker network create pg_net
```
Создаем файл .env, из которого docker compose будет брать информацию о сети
```bash
echo "PG_NET=$(docker network inspect pg_net -f '{{range .IPAM.Config}}{{.Subnet}}{{end}}')" > .env
```
Запустить контейнеры pg_master, pg_slave, pg_asyncslave, pg_standalone и все остальные сервисы.
```bash
docker compose up -d
```

***
## Логическая репликация
По умолчанию в конфиге pg_master установлен wal_level=logical.  
Как создать публикацию на pg_master и подписку на pg_standalone.
```sql
-- On pg_master
CREATE TABLE test (
    id SERIAL PRIMARY KEY,
    data TEXT
);
GRANT CONNECT ON DATABASE user TO replicator;
GRANT SELECT ON public.test TO replicator;
GRANT USAGE ON SCHEMA public TO replicator;


CREATE PUBLICATION my_publication FOR TABLE test;
```
```sql
-- On pg_standalone
CREATE TABLE test (
    id SERIAL PRIMARY KEY,
    data TEXT
);
GRANT CONNECT ON DATABASE user TO replicator;
GRANT SELECT ON public.test TO replicator;
GRANT USAGE ON SCHEMA public TO replicator;


CREATE SUBSCRIPTION my_subscription
CONNECTION 'host=pg_master port=5432 dbname=user user=replicator password=pass'
PUBLICATION my_publication;

```
