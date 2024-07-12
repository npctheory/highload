## О проекте
Домашнее задание по репликации.  
Проект состоит из следующих компонентов:  
* Приложение Spring Boot в папке ./app, которое собирается в образ и контейнер **app**  
* Бэкап postgres в папке ./db, который собирается в образ db:local. На его основе создаются контейнеры **pg_master**, **pg_slave**, **pg_asyncslave**  
* Контейниризованный **ansible** в папке ./ansible. В папке ./ansible/playbooks плейбуки, через которые можно настраивать репликацию.
* **PGAdmin**, **Prometheus**, **Postgres-экспортеры**, **Grafana**, которые подключаются в docker-compose.
## Начало работы
Создать docker-сеть:
```bash
docker network create pg_net
```
Находясь в папке highload запустить docker-compose:
```bash
docker compose up -d
```
## Настройка репликации
Репликация настраивается через плейбуки ansible. Ansible находится в отдельном контейнере.
```bash
docker exec -it ansible bash
```
Выполнить один из плейбуков:  

1. Контейнер pg_master становится primary. Контейнеры pg_slave и pg_asyncslave в режиме standby (async)
```bash
ansible-playbook playbooks/pg_master_is_primary.yml
```
2. Контейнер pg_slave становится primary.
```bash
ansible-playbook playbooks/pg_slave_is_primary.yml
```
