docker network create pg_net

docker compose -f docker-compose-create-backup.yml up -d

docker exec -it -e PG_NET=$(docker network inspect pg_net -f '{{range .IPAM.Config}}{{.Subnet}}{{end}}') pg_master bash

echo "ssl = off" | tee -a "$PGDATA/postgresql.conf" > /dev/null &&
echo "wal_level = replica" | tee -a "$PGDATA/postgresql.conf" > /dev/null &&
echo "max_wal_senders = 4" | tee -a "$PGDATA/postgresql.conf" > /dev/null

echo "host replication replicator $PG_NET md5" >> "$PGDATA/pg_hba.conf"

grep -E '^(ssl|wal_level|max_wal_senders)\s*=' /var/lib/postgresql/data/postgresql.conf || echo "Parameters not found." &&
tail -n 1 /var/lib/postgresql/data/pg_hba.conf

exit


docker exec -it pg_master su - postgres -c psql

create role replicator with login replication password 'pass';
SELECT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'replicator');

exit;

docker-compose -f docker-compose-create-backup.yml restart pg_master

docker exec -it pg_master bash

pg_basebackup -h pg_master -D /pg_slave -U replicator -v -P --wal-method=stream
pass

cp -r /pg_slave/* /pg_asyncslave/

exit

touch volumes/pg_slave/standby.signal &&
echo "primary_conninfo = 'host=pg_master port=5432 user=replicator password=pass application_name=pg_slave'" >> volumes/pg_slave/postgresql.conf

tail -n 1 volumes/pg_slave/postgresql.conf

echo "primary_conninfo = 'host=pg_master port=5432 user=replicator password=pass application_name=pg_asyncslave'" >> volumes/pg_asyncslave/postgresql.conf &&
touch volumes/pg_asyncslave/standby.signal

tail -n 1 volumes/pg_asyncslave/postgresql.conf

docker compose -f docker-compose-create-backup.yml down
docker compose up -d

docker exec -it pg_master su - postgres -c psql
select application_name, sync_state from pg_stat_replication;
exit

echo "synchronous_commit = on" | tee -a volumes/pg_master/postgresql.conf > /dev/null &&
echo "synchronous_standby_names = 'FIRST 1 (pg_slave, pg_asyncslave)'" | tee -a volumes/pg_master/postgresql.conf > /dev/null

docker exec -it pg_master su - postgres -c psql

select pg_reload_conf();
select application_name, sync_state from pg_stat_replication;

exit

**Создадим тестовую таблицу на pgmaster и проверим репликацию**
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

**19**
docker exec -it pg_slave su - postgres -c psql
insert into test(id) values(2);
exit;
**20**
docker stop pg_asyncslave

docker exec -it pg_master su - postgres -c psql

select application_name, sync_state from pg_stat_replication;

insert into test(id) values(2);

select * from test;

exit;

docker exec -it pg_slave su - postgres -c psql
select * from test;
exit;
**21**
docker stop pg_slave

docker exec -it pg_master su - postgres -c psql

select application_name, sync_state from pg_stat_replication;

insert into test(id) values(3);

docker start pg_slave

docker start pg_asyncslave

docker stop pg_master

**24**
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