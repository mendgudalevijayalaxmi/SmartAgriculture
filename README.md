# Smart Agriculture

A clean Spring Boot REST API foundation for maintaining crop information. The repository originally contained only a standalone Java console stub; this version establishes a working Spring Boot application with a small crop-management API.

## Technology choices

The project uses **PostgreSQL (SQL) with Spring Data JPA**. Crop records have stable, structured fields, validation rules, and expected relationships to farms, seasons, and harvests. A relational database makes those constraints and future relationships explicit. MongoDB would be a better choice only if the main records were highly variable documents with few cross-record relationships.

Spring Boot manages dependency versions. Flyway owns the schema, so Hibernate validates the schema instead of changing it automatically. Tests use an isolated in-memory H2 database.

## Requirements

- Java 21 or newer
- Maven 3.6.3 or newer
- Docker Compose for the local PostgreSQL option

## Run locally

Start PostgreSQL:

```sh
docker compose up -d db
```

Run the API:

```sh
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. The local Compose credentials are for development only; set secure credentials in deployed environments.

The database connection can be configured with `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD`. Defaults are intended only for the local Compose setup.

## API

- `GET /api/v1/crops` — list crops
- `GET /api/v1/crops/{id}` — get one crop
- `POST /api/v1/crops` — create a crop
- `PUT /api/v1/crops/{id}` — replace a crop
- `DELETE /api/v1/crops/{id}` — delete a crop
- `GET /actuator/health` — application health

Example:

```sh
curl -X POST http://localhost:8080/api/v1/crops \
  -H 'Content-Type: application/json' \
  -d '{"name":"Maize","variety":"Hybrid","season":"KHARIF","soilType":"Loamy","expectedYieldKgPerHectare":5200.00,"notes":"Example crop record"}'
```

`season` accepts `KHARIF`, `RABI`, `ZAID`, or `PERENNIAL`. Invalid input returns HTTP 400 with field-level validation details. Missing crop IDs return HTTP 404.

## Test

```sh
mvn test
```

The integration tests start the application against a fresh H2 database and run the same Flyway schema migration used by PostgreSQL.

## Deploy to Render

The repository includes a `Dockerfile` and `render.yaml` Blueprint. Connect this repository to Render as a Blueprint and apply the configuration to create the API and PostgreSQL database in Singapore. The Docker build runs `mvn verify`; Render uses `/actuator/health` as the service health check. Database connection values are provided by Render and are not stored in the repository.

The Blueprint currently selects Render's **free** plans for a demo. Free web services spin down after 15 minutes without traffic and can take about a minute to wake. Free PostgreSQL has a 1 GB limit, no backups, and expires 30 days after creation; after a further 14-day grace period, Render deletes the database and its data. Do not store important or production data on this configuration. For production, select paid plans and confirm current pricing in Render before creating resources.

See [Render's free instance limitations](https://render.com/docs/free) and [Blueprint documentation](https://render.com/docs/blueprint-spec) for current platform details.
