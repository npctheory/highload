## Начало работы
Для запуска проекта вам потребуется Docker и Ansible с Community.Docker
```bash
pip install ansible
ansible-galaxy collection install community.docker
```
## Как запусить проект
Собрать docker-образ, который будет базовым для контейнеров postgres
```bash
docker build -t pg_db:local ./db

docker run -d \
  --name pg_master \
  -e POSTGRES_DB=user \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  pg_db:local

```
Создать сеть
```bash
docker network create pg_net
```
Создать файл .env, из которого docker-compose будет брать информацию о сети
```bash
echo "PG_NET=$(docker network inspect pg_net -f '{{range .IPAM.Config}}{{.Subnet}}{{end}}')" > .env
```
Запустить docker-compose
```bash
docker compose up -d
```
```bash
ansible-playbook playbooks/debug_pg.yml
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
