# Hospital Management System — Backend

Modular-monolith Spring Boot backend for a Hospital Management System.
Designed so individual modules (patients, doctors, appointments, billing, etc.)
stay loosely coupled today, and can be peeled off into separate microservices
later without a rewrite.

**Status:** ~20% complete. `patient` is a fully implemented reference module.
Everything else is schema-only — see [`TICKETS.md`](./TICKETS.md) for the backlog.

---

## Tech stack

| Concern        | Choice                          |
|-----------------|----------------------------------|
| Language        | Java 21                          |
| Framework       | Spring Boot 3.3.x                |
| Data access     | Spring Data JPA + Hibernate      |
| Database        | MySQL 8                          |
| Migrations      | Flyway                           |
| Build           | Maven                            |
| Boilerplate     | Lombok                           |
| Validation      | Jakarta Bean Validation          |
| API testing     | Postman (`/postman` collection)  |

Security (Spring Security / auth) is **intentionally not included yet** — it's
a separate phase, added once the core domain modules exist. Don't add
`spring-boot-starter-security` yourself; that'll be a dedicated set of tickets.

---

## Project structure

We use **package-by-feature**, not package-by-layer. Each business capability
(`patient`, `doctor`, `appointment`, ...) is a self-contained package with its
own `entity`, `repository`, `dto`, `mapper`, `service`, `controller`. This is
what makes it realistic to later cut a module out into its own microservice —
everything it needs lives in one place.

```
com.hospital.hms
├── common/                  # Cross-cutting code shared by every module
│   ├── entity/BaseEntity.java
│   ├── exception/           # Custom exceptions + GlobalExceptionHandler
│   └── config/              # App-wide configuration beans
│
├── patient/                 # ✅ FULLY IMPLEMENTED — use this as your template
│   ├── entity/
│   ├── repository/
│   ├── dto/
│   ├── mapper/
│   ├── service/
│   └── controller/
│
├── doctor/                  # 🚧 your ticket — package doesn't exist yet
├── department/               # 🚧 your ticket
├── appointment/               # 🚧 your ticket
├── staff/                     # 🚧 your ticket
├── ward/                      # 🚧 your ticket (wards + beds)
├── admission/                  # 🚧 your ticket
├── medicalrecord/               # 🚧 your ticket (records + prescriptions)
└── billing/                     # 🚧 your ticket (invoices + invoice items)
```

The **database schema for the entire domain already exists** (see
`src/main/resources/db/migration/V1__init_schema.sql`). This is deliberate —
in a real project, the data model is usually agreed on early because tables
reference each other via foreign keys, and changing that later is expensive.
Your job on each ticket is to build the Java layer (entity → repository →
service → controller) on top of tables that are already there.

---

## Conventions (read before writing code)

These are the same conventions applied throughout `patient` — follow them
in every module you build, so the codebase stays consistent as it grows.

1. **Entities extend `BaseEntity`** — don't redeclare `id`/`createdAt`/`updatedAt`.
2. **DTOs are Java records**, never expose entities directly over the API.
   Split `XRequest` (input, with `@Valid` annotations) from `XResponse` (output).
3. **Mapping is manual**, via a `@Component` mapper class (`XMapper`), not
   entities' constructors and not MapStruct (yet — that's a separate future
   decision for the whole team, not a per-module one).
4. **Services are interface + impl.** Controllers depend on the interface.
5. **Business rule violations throw custom exceptions**
   (`ResourceNotFoundException`, `DuplicateResourceException`, ...) —
   never handle errors with try/catch in the controller. Add new exception
   types to `common.exception` and a handler method in `GlobalExceptionHandler`
   as needed.
6. **`ddl-auto: validate`, always.** Schema changes go through a new Flyway
   file (`V3__...`, `V4__...`), never by editing an already-applied migration
   and never by letting Hibernate auto-generate DDL.
7. **Controllers stay thin.** No business logic in `@RestController` classes —
   validation, orchestration and rules belong in the service layer.
8. **Pagination** for any "get all" endpoint that could return more than a
   page or two of data — see `PatientController#getAllPatients` for the
   `Pageable` pattern.
9. **REST paths**: `/api/v1/{plural-resource}`, e.g. `/api/v1/doctors`.

---

## Getting started

### Prerequisites
- JDK 21
- Maven 3.9+
- MySQL 8 running locally (or update `application-dev.yml` to point elsewhere)

### 1. Create the database (Flyway will also auto-create it, but explicit is fine)
```sql
CREATE DATABASE IF NOT EXISTS hms_db;
```

### 2. Configure credentials
Either edit `src/main/resources/application-dev.yml` directly, or set env vars:
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

### 3. Run
```bash
mvn spring-boot:run
```
Flyway will run `V1__init_schema.sql` and `V2__seed_departments.sql`
automatically on startup — check the logs to confirm.

### 4. Try it
```bash
curl -X POST http://localhost:8080/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@example.com",
    "phone": "9876543210",
    "dateOfBirth": "1990-05-20",
    "gender": "FEMALE",
    "bloodGroup": "O_POSITIVE",
    "address": "123 Main St",
    "emergencyContactName": "John Doe",
    "emergencyContactPhone": "9876500000"
  }'
```
Or import `postman/hms-backend.postman_collection.json` into Postman.

### Run tests
```bash
mvn test
```
See `PatientServiceImplTest` for the service-layer testing pattern
(Mockito, no Spring context needed for pure unit tests).

---

## How to work through the backlog

1. Open [`TICKETS.md`](./TICKETS.md), pick the next unstarted ticket **in
   order** — later tickets often assume earlier ones exist (e.g. the
   Appointment module needs Doctor + Patient first).
2. Create a branch per ticket: `feature/HMS-102-doctor-module`.
3. Follow the `patient` package as your structural template.
4. Write at least one service-layer test per ticket (see
   `PatientServiceImplTest`).
5. Add/update the Postman collection for any new endpoints.
6. Open a PR against `main` with the ticket ID in the title.

Treat `patient` as the "golden example" — if you're ever unsure how
something should look, check how it was done there first.
