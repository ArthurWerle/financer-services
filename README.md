# Overengineering my self-hosted personal finances control app with microservices for study purposes

PS: this is CRAZY overengineering for a simple app with JUST ONE USER (me), and ONE PERSON WORKING ON (also me xD) of course I know that, but I wanted to create this app anyways... so I took the opportunity to study some things like self-hosting, homelab, portainer, docker, docker-compose, micro services architecture, kotlin, spring boot, golang and many more.

## Services

### category-service

- a micro service written in Golang to serve as a crud for all my categories and types because.... why not?

### transaction-service

- a micro service written in Kotlin using Spring Boot to handle all my transactions and recurring transactions

### bff-service

- a Back-end for Front-end service written in NodeJS 20 using typescript and express to serve as my BFF orchestrating my API calls.

## Details

- `docker-compose.yml` is looking for a `stack.env` because `Portainer` (which is the tool I'm using to self-host it) needs the file to be named that way to overwrite it.

## Running the app

- `docker compose up --build`
