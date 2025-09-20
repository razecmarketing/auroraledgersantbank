# Local Data Stack (Postgres + Mongo + Redis)

- Compose file: `docker-compose.yml`
- Env file: `.env`

## Quick start

1. Start containers
```
docker compose up -d
```

2. Check health
```
docker compose ps
```

3. Stop
```
docker compose down
```

## Connection details

- Postgres: localhost:5432, db: `santander` user: `santander` pass: `santander`
- MongoDB: localhost:27017, user: `root` pass: `root`
- Redis: localhost:6379 (no password)

## Notes
- Volumes persist data across restarts.
- Healthchecks ensure services are ready before app connects.
