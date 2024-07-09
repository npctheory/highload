  
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
Проверяем что обе реплики работают в режиме async
```bash
docker exec -it pg_master su - postgres -c psql
select application_name, sync_state from pg_stat_replication;
exit
```
Делаем на pg_master настройку, что первая реплика из списка должна быть синхронной.
```bash
echo "synchronous_commit = on" | tee -a volumes/pg_master/postgresql.conf > /dev/null &&
echo "synchronous_standby_names = 'FIRST 1 (pg_slave, pg_asyncslave)'" | tee -a volumes/pg_master/postgresql.conf > /dev/null
```
Проверяем, чтобы pg_slave был sync а pg_asyncslave был potential
```bash
docker exec -it pg_master su - postgres -c psql

select pg_reload_conf();
select application_name, sync_state from pg_stat_replication;

exit
```
**Проверка**
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
***
## Меняем местами pg_master и pg_slave
docker exec -it pg_slave su - postgres -c psql

select pg_promote();

exit;

docker exec -it pg_slave su - postgres -c psql

insert into test(id) values(4);

exit;

head -n -2 volumes/pg_slave/postgresql.conf > volumes/pg_slave/postgresql.conf.tmp && mv volumes/pg_slave/postgresql.conf.tmp volumes/pg_slave/postgresql.conf &&
echo "synchronous_commit = on" | tee -a volumes/pg_slave/postgresql.conf > /dev/null &&
echo "synchronous_standby_names = 'FIRST 1 (pg_master, pg_asyncslave)'" | tee -a volumes/pg_slave/postgresql.conf > /dev/null

docker exec -it pg_slave su - postgres -c psql

select pg_reload_conf();

exit;
