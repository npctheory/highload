## О проекте
Домашнее задание по репликации.  
Проект состоит из следующих компонентов:  
* Приложение Spring Boot в папке ./app, которое собирается в образ app:local и контейнер **app**  
* Бэкап postgres в папке ./db, который собирается в образ db:local. На его основе создаются контейнеры **pg_master**, **pg_slave**, **pg_asyncslave**  
* Контейниризованный **ansible** в папке ./ansible. В папке ./ansible/playbooks плейбуки, через которые можно настраивать репликацию.
* **cAdvisor**, который подключается в docker-compose.
## Начало работы
Создать docker-сеть:
```bash
docker network create pg_net
```
Находясь в папке highload запустить docker-compose:
```bash
docker compose up -d
```
Репликация настраивается через плейбуки ansible. Ansible находится в отдельном контейнере.
Выберите один из плейбуков:  

---
## 1. Асинхронная репликация
Плейбук async.yml настраивает асинхронную репликацию, в которой pg_master - primary, а pg_slave - secondary.  
В этой конфигурации методы приложения getProfileById и searchProfiles обращаются к pg_slave.
```bash
docker exec -it ansible bash
```
```bash
ansible-playbook playbooks/async.yml
```
Видео:  

[async.webm](https://github.com/user-attachments/assets/9367cdd0-e272-4e4b-9f18-3a539cc66e8c)

---
## 2. Кворумная репликация
Плейбук quorum.yml настраивает кворумную репликацию, в которой pg_master - primary, а pg_slave, pg_asyncslave - secondary.  
Значение synchronous_standby_names на pg_master становится ANY 1 (pg_slave, pg_asyncslave).
В этой конфигурации метод приложения getProfileById обращается к pg_slave, а метод searchProfiles к pg_asyncslave.
```bash
docker exec -it ansible bash
```
```bash
ansible-playbook playbooks/quorum.yml
```
Отключение pg_master, промоушн pg_slave и перенастройка pg_asyncslave на получение WAL от pg_slave.
```bash
ansible-playbook playbooks/quorum_off.yml
```
Видео:  

[quorum.webm](https://github.com/user-attachments/assets/a206af4c-96e2-47fc-92dd-4418e3b04cdb)

