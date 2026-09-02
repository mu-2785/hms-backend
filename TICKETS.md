# HMS Backend — Ticket Backlog

Work these roughly in order — later modules assume earlier ones exist.
Each ticket follows the same shape a real ticket would: context, scope,
acceptance criteria. Nothing here tells you line-by-line what code to write —
that's the point. Use the `patient` module as your structural reference.

Story points are relative sizing (1 = few hours, 5 = multi-day), not a promise.

---

## Sprint 1 — Core reference data & provider modules

### HMS-101 — Department module (3 pts)
**Context:** `departments` table already exists and is seeded (V2 migration).
Doctors and staff both reference it, so this should land first.

**Scope:**
- `Department` entity (extends `BaseEntity`)
- `DepartmentRepository`
- `DepartmentRequest` / `DepartmentResponse` DTOs
- `DepartmentMapper`
- `DepartmentService` (+ impl)
- `DepartmentController`: full CRUD + `GET /api/v1/departments` (paginated)

**Acceptance criteria:**
- [ ] Creating a department with a name that already exists returns `409 Conflict`
- [ ] Endpoints match the `/api/v1/departments` convention
- [ ] Deleting a department that has doctors/staff assigned to it should fail
      gracefully (decide: block the delete, or leave it in for now and file a
      follow-up ticket — document your decision in the PR description)
- [ ] Unit tests for the service layer (success + at least one failure path)

---

### HMS-102 — Doctor module (5 pts)
**Context:** Depends on HMS-101. `doctors` table has a required FK to
`departments`.

**Scope:**
- `Doctor` entity, repository, DTOs, mapper, service, controller
- `DoctorRequest` should accept `departmentId` (not a nested department
  object) — look up and validate the department exists in the service layer
- `GET /api/v1/doctors?departmentId=` and `?specialization=` filters
- `GET /api/v1/doctors/{id}` should return department info in the response
  (you decide: embed a small `DepartmentSummary`, or just `departmentId` +
  `departmentName` flattened onto `DoctorResponse` — either is fine, pick one
  and be consistent)

**Acceptance criteria:**
- [ ] Creating a doctor with a non-existent `departmentId` returns `404`
- [ ] Duplicate email returns `409`
- [ ] Filtering by department and by specialization both work and are covered
      by at least one test each

---

### HMS-103 — Staff module (3 pts)
**Context:** Depends on HMS-101. Similar shape to Doctor but simpler —
`department_id` is nullable here (some staff, e.g. general admin, aren't
tied to one department).

**Scope:**
- Full CRUD following the same pattern as Doctor
- `role` field should be a Java enum (`NURSE`, `RECEPTIONIST`,
  `LAB_TECHNICIAN`, `PHARMACIST`, `ADMIN`) — matches the `VARCHAR` column,
  mapped the same way `Gender`/`BloodGroup` are mapped on `Patient`
- `GET /api/v1/staff?role=` filter

**Acceptance criteria:**
- [ ] Invalid `role` value in the request body returns a `400` with a clear
      validation message (not a raw deserialization stack trace — check how
      `PatientRequest` enums behave and match that)

---

## Sprint 2 — Scheduling

### HMS-104 — Ward & Bed module (5 pts)
**Context:** `wards` and `beds` are two tables, one parent-child
relationship. This is your first time modeling a `@OneToMany` in this
codebase — decide whether to expose beds only through the ward endpoints, or
give beds their own top-level `/api/v1/beds` too. Document the choice.

**Scope:**
- `Ward` entity with `@OneToMany` to `Bed` (or model it as two independent
  aggregates with `wardId` on `Bed` and no JPA-level relationship at all —
  both are legitimate; the ticket is intentionally open on this so you make
  the call and justify it in the PR)
- `POST /api/v1/wards` — also consider: should creating a ward with
  `totalBeds: 10` auto-generate 10 `Bed` rows, or should beds be created
  individually via a separate endpoint? Pick one.
- `PATCH /api/v1/beds/{id}/occupy` and `/release` — toggling `is_occupied`
  is a common enough operation to deserve its own endpoint rather than going
  through a full `PUT`

**Acceptance criteria:**
- [ ] Can't create a bed with a `bedNumber` that already exists in the same ward
      (matches the DB's `uq_beds_ward_bed_number` constraint — the API should
      return a clean `409`, not let the DB constraint violation bubble up as
      a raw `500`)

---

### HMS-105 — Appointment module (5 pts)
**Context:** Depends on Patient (done) + Doctor (HMS-102). This is the first
module where you're validating relationships between two other modules at
once.

**Scope:**
- `Appointment` entity/repository/DTOs/mapper/service/controller
- `AppointmentRequest` takes `patientId` + `doctorId`, validate both exist
- `status` enum: `SCHEDULED`, `COMPLETED`, `CANCELLED`, `NO_SHOW`
- Business rule: a doctor can't have two `SCHEDULED` appointments at the
  exact same `appointmentDate` + `appointmentTime` — enforce this in the
  service layer and throw a clear custom exception (you'll need a new
  exception type + handler; follow the pattern of
  `DuplicateResourceException`)
- `PATCH /api/v1/appointments/{id}/status` — dedicated endpoint for status
  transitions rather than a full update
- `GET /api/v1/appointments?doctorId=&date=` and `?patientId=`

**Acceptance criteria:**
- [ ] Double-booking a doctor at the same date+time is rejected with a `409`
- [ ] Status transition endpoint validates the transition makes sense (e.g.
      you probably shouldn't be able to move a `CANCELLED` appointment back
      to `SCHEDULED` — decide the allowed transitions and enforce them)
- [ ] Tests cover the double-booking rule specifically

---

## Sprint 3 — Clinical & admissions

### HMS-106 — Admission module (5 pts)
**Context:** Depends on Patient, Doctor, and Ward/Bed (HMS-104). Models an
in-patient stay.

**Scope:**
- Full CRUD/lifecycle for `Admission`
- On admit: mark the chosen `Bed` as occupied (`is_occupied = true`) — this
  touches another module's data, so think about where that logic belongs
  (in `AdmissionService`, calling `BedRepository`/`BedService`? via an
  application event? Pick something and be able to explain the tradeoff.)
- On discharge (`PATCH /api/v1/admissions/{id}/discharge`): set
  `dischargeDate`, flip `status` to `DISCHARGED`, and release the bed
  (`is_occupied = false`)
- Reject admitting a patient to a bed that's already occupied — `409`

**Acceptance criteria:**
- [ ] Admitting into an occupied bed fails cleanly
- [ ] Discharging releases the bed (verify with a test that re-fetches the bed)
- [ ] Can't discharge an already-discharged admission

---

### HMS-107 — Medical Records & Prescriptions module (5 pts)
**Context:** Depends on Patient, Doctor, Appointment (optional FK).
Two related tables (`medical_records` 1—N `prescriptions`) — similar
parent/child shape to HMS-104's ward/beds decision. Apply what you learned
there.

**Scope:**
- `MedicalRecord` CRUD, with `PrescriptionRequest` items nested inside
  `MedicalRecordRequest` (creating a record and its prescriptions in one call)
- `GET /api/v1/patients/{patientId}/medical-records` — a patient's full
  history, most recent first
- Consider: should this data be readable by anyone, or eventually restricted
  to the treating doctor / the patient themselves? Not for this ticket (no
  security yet) — but leave a `// TODO(security):` comment where access
  control will eventually need to plug in, so it's easy to find later.

**Acceptance criteria:**
- [ ] Creating a medical record with 2+ prescriptions in a single request
      persists all of them correctly (verify the FK is set on each)
- [ ] `GET` history endpoint is paginated and sorted by `recordDate DESC`

---

## Sprint 4 — Billing

### HMS-108 — Billing module (5 pts)
**Context:** Depends on Patient, and optionally Appointment/Admission.
Two tables: `invoices` 1—N `invoice_items`.

**Scope:**
- `Invoice` CRUD with nested `invoiceItems` on create (same pattern as
  HMS-107's nested prescriptions)
- `totalAmount` should be **computed server-side** from the sum of
  `invoiceItems` (quantity × amount) — never trust a client-supplied total
- `PATCH /api/v1/invoices/{id}/status` for `PENDING → PAID → CANCELLED`
  transitions, with the same "validate the transition" thinking as HMS-105
- `GET /api/v1/patients/{patientId}/invoices?status=`

**Acceptance criteria:**
- [ ] `totalAmount` in the response always equals the sum of its items,
      even if a client tries to pass a different value in the request
- [ ] Can't mark a `CANCELLED` invoice as `PAID`

---

## Cross-cutting / tech-debt tickets (pick up anytime)

### HMS-201 — API documentation with springdoc-openapi (2 pts)
Add `springdoc-openapi-starter-webmvc-ui`, verify `/swagger-ui.html` reflects
every module you've built so far, and add `@Operation`/`@Schema` annotations
where the generated docs aren't self-explanatory.

### HMS-202 — Global pagination defaults & response headers (2 pts)
Right now `PatientController` hardcodes `@PageableDefault(size = 20)` per
endpoint. Extract this into shared config so every module gets the same
default page size/sort without repeating the annotation everywhere.

### HMS-203 — Integration tests with Testcontainers (3 pts)
The existing `PatientServiceImplTest` is a pure unit test (mocked
repository). Add a `@SpringBootTest` + Testcontainers MySQL integration test
for at least the Patient and Appointment modules that hits a real (ephemeral)
database, to catch things unit tests can't (actual constraint violations,
query correctness).

### HMS-204 — Auditing: who created/updated a record (3 pts)
`BaseEntity` currently tracks `createdAt`/`updatedAt` but not *who*. Add
`createdBy`/`updatedBy` columns (new Flyway migration) — for now they can be
populated with a hardcoded system value; wiring them to a real logged-in user
is blocked on the security phase, but the plumbing can go in now.

### HMS-205 — Centralize enum validation error messages (2 pts)
Right now an invalid enum value in a JSON request body produces a fairly
raw Jackson error message via `GlobalExceptionHandler#handleUnexpected`.
Add a dedicated `@ExceptionHandler(HttpMessageNotReadableException.class)`
that returns a `ProblemDetail` as clean as the bean-validation one does.

---

## Not in scope yet (future phases — don't build these)
- Spring Security / authentication / authorization
- Notifications (email/SMS reminders for appointments)
- File uploads (lab reports, scanned documents)
- Reporting/analytics endpoints
- Splitting any module out into an actual separate microservice
