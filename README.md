  
## Физическая Репликация (Streaming Replication)
**Создаем сеть и тома**
```bash
docker network create pg_net
```
```bash
echo "PG_NET=$(docker network inspect pg_net -f '{{range .IPAM.Config}}{{.Subnet}}{{end}}')" > .env
```
```bash
docker compose up -d
```

***
## Логическая репликация
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

## Проверка
Создаем тестовые таблицы
```bash
docker exec -it pg_master su - postgres -c psql

create table test(id bigint primary key not null);
insert into test(id) values(1);
select * from test;
exit;

docker exec -it pg_slave su - postgres -c psql

select * from test;

exit;


docker exec -it pg_asyncslave su - postgres -c psql

select * from test;

exit;
```

Проверяем что INSERT на слейве выдает ошибку
```bash
docker exec -it pg_slave su - postgres -c psql
insert into test(id) values(2);
exit;
```
Проверяем что без асинхронного слейва pg_master продолжает работать.
```
docker stop pg_asyncslave

docker exec -it pg_master su - postgres -c psql

select application_name, sync_state from pg_stat_replication;

insert into test(id) values(2);

select * from test;

exit;
```
Без синхронного слейва pg_master должен вешаться на INSERT
```bash
docker exec -it pg_slave su - postgres -c psql
select * from test;
exit;

docker stop pg_slave

docker exec -it pg_master su - postgres -c psql

select application_name, sync_state from pg_stat_replication;

insert into test(id) values(3);
```
```bash
docker start pg_slave

docker start pg_asyncslave

docker stop pg_master
```