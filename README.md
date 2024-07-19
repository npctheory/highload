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
Находясь в папке highload-replication запустить docker-compose:
```bash
docker compose up -d
```
Репликация настраивается через плейбуки ansible. Ansible находится в отдельном контейнере.
Выберите один из плейбуков:  

---
## 1. Асинхронная репликация
Начальное состояние системы: серверы pg_master и pg_slave работают независимо. Все методы приложения Spring Boot используют БД pg_master.  
Плейбук async.yml настраивает асинхронную репликацию, в которой pg_master - primary, pg_slave - secondary а также изменяет конфигурационный файл приложения Spring Boot - БД pg_slave становится дата-сорсом, который используется методами getProfileById и searchProfiles.  
Для нагрузки на чтение используется тест read.jmx.  
Выполнение плейбука:  
```bash
docker exec -it ansible bash
```
```bash
ansible-playbook playbooks/async.yml
```
На видео показано, как до выполнения плейбука в cAdvisor использование памяти контейнером pg_master превышает тот же показатель на pg_slave в 10 раз. После выполнения плейбука уже pg_slave использует в 10 раз больще памяти чем pg_master:  

[async.webm](https://github.com/user-attachments/assets/9367cdd0-e272-4e4b-9f18-3a539cc66e8c)

---
## 2. Кворумная репликация
Начальное состояние системы: серверы pg_master, pg_slave, pg_asyncslave работают независимо.  
Плейбук quorum1.yml настраивает кворумную репликацию, в которой pg_master - primary, а pg_slave, pg_asyncslave - secondary.  
Значение synchronous_standby_names на pg_master становится ANY 1 (pg_slave, pg_asyncslave).  
Для нагрузки на чтение используется тест read.jmx.  
```bash
docker exec -it ansible bash
```
```bash
ansible-playbook playbooks/quorum1.yml
```

[balanced.webm](https://github.com/user-attachments/assets/cb20d1ad-96cd-4a91-aa66-0f9f0af55ea6)


Плейбук quorum2.yml: После того как репликация настроена, мы создаем новых пользователей на мастере, отключаем pg_asynclsave, останавливаем создание пользователей, промоутим pg_slave до primary и перенастраиваем pg_master и pg_asyncslave на получение WAL от pg_slave. Перед перезагрузкой смотрим количество пользователей в таблицах.
Для нагрузки на запись используется тест userRegister.jmx.  
```bash
ansible-playbook playbooks/quorum2.yml
```
На видео показано, что после отключения контейнера с репликой pg_asyncslave возникает расхождение в количестве записей на pg_slave и pg_asyncslave. После промоушена pg_slave количество записей выравнивается:     

[quorum.webm](https://github.com/user-attachments/assets/a206af4c-96e2-47fc-92dd-4418e3b04cdb)

