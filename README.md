# Project Setup

This project contains two main components:
1. A Spring Boot application located in the `/src` directory.
2. A PostgreSQL configuration located in the `/db` directory.

## Building the Docker Images

To build both Docker images using a single command, you can use the following instructions:

1. **Navigate to the root directory of your project**:
   
```bash
docker build -t springboot-app ./src && docker build -t custom-postgres ./db
```

This command does the following:
```bash
    docker build -t springboot-app ./src
```
builds the Docker image for the Spring Boot application located in the /src directory and tags it as springboot-app.
```bash
    docker build -t custom-postgres ./db
```
builds the Docker image for the PostgreSQL configuration located in the /db directory and tags it as custom-postgres.

```bash
docker-compose up
```