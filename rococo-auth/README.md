# rococo-auth (Kotlin, Spring Authorization Server)

A minimal auth service inspired by *niffler-auth*, rewritten in Kotlin and renamed to **rococo**.

## What's included
- Spring Boot 3.4 + Kotlin + JPA (MySQL)
- Spring Authorization Server (OAuth2/OIDC endpoints)
- Form login + registration page
- Flyway migrations
- Docker Compose for MySQL

## Quick start
```bash
docker compose up -d # starts MySQL 8.4
./gradlew bootRun
```

App runs at http://localhost:9000

## Mapping from *niffler-auth* to *rococo-auth*
- Package: `guru.qa.niffler` -> `guru.qa.rococo`
- App name: `niffler-auth` -> `rococo-auth`
- DB: uses `rococo_auth` (Flyway manages schema)
- Kafka/Vault removed for minimal start. Add later if needed.
```