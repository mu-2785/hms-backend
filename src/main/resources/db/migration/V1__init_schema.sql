-- ============================================================================
-- V1__init_schema.sql
-- Initial schema for Hospital Management System.
--
-- NOTE FOR THE TEAM:
-- The full domain schema is created up front (even though most modules are
-- not implemented in code yet). This mirrors how schema design usually works
-- in a real project: the data model is agreed on early because tables have
-- foreign keys into each other, and changing it later is expensive.
-- As you pick up tickets, you'll write the JPA entities/repositories that map
-- onto the tables that already exist here. Do NOT let Hibernate auto-generate
-- DDL (ddl-auto=validate) — all schema changes must go through a new Flyway
-- migration file (V2__..., V3__..., etc). Never edit this file after it has
-- been applied to a shared database.
-- ============================================================================

-- ---------------------------------------------------------------------------
-- DEPARTMENTS
-- ---------------------------------------------------------------------------
CREATE TABLE departments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_departments_name UNIQUE (name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- PATIENTS  (reference module — fully implemented in code, see patient package)
-- ---------------------------------------------------------------------------
CREATE TABLE patients (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name                  VARCHAR(100) NOT NULL,
    last_name                   VARCHAR(100) NOT NULL,
    email                       VARCHAR(150) NOT NULL,
    phone                       VARCHAR(20)  NOT NULL,
    date_of_birth               DATE NOT NULL,
    gender                      VARCHAR(20)  NOT NULL,
    blood_group                 VARCHAR(5),
    address                     VARCHAR(255),
    emergency_contact_name      VARCHAR(150),
    emergency_contact_phone     VARCHAR(20),
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_patients_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE INDEX idx_patients_last_name ON patients (last_name);
CREATE INDEX idx_patients_phone ON patients (phone);

-- ---------------------------------------------------------------------------
-- DOCTORS
-- ---------------------------------------------------------------------------
CREATE TABLE doctors (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL,
    phone               VARCHAR(20)  NOT NULL,
    specialization      VARCHAR(100) NOT NULL,
    department_id       BIGINT NOT NULL,
    consultation_fee    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    years_of_experience INT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_doctors_email UNIQUE (email),
    CONSTRAINT fk_doctors_department FOREIGN KEY (department_id) REFERENCES departments (id)
) ENGINE=InnoDB;

CREATE INDEX idx_doctors_department_id ON doctors (department_id);
CREATE INDEX idx_doctors_specialization ON doctors (specialization);

-- ---------------------------------------------------------------------------
-- STAFF  (non-doctor hospital staff: nurses, receptionists, lab techs, etc.)
-- ---------------------------------------------------------------------------
CREATE TABLE staff (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    role            VARCHAR(30)  NOT NULL, -- NURSE, RECEPTIONIST, LAB_TECHNICIAN, PHARMACIST, ADMIN
    department_id   BIGINT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_staff_email UNIQUE (email),
    CONSTRAINT fk_staff_department FOREIGN KEY (department_id) REFERENCES departments (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- WARDS & BEDS
-- ---------------------------------------------------------------------------
CREATE TABLE wards (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    ward_type       VARCHAR(30) NOT NULL, -- GENERAL, ICU, PRIVATE, EMERGENCY
    floor_number    INT NOT NULL,
    total_beds      INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE beds (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id         BIGINT NOT NULL,
    bed_number      VARCHAR(20) NOT NULL,
    is_occupied     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_beds_ward FOREIGN KEY (ward_id) REFERENCES wards (id),
    CONSTRAINT uq_beds_ward_bed_number UNIQUE (ward_id, bed_number)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- APPOINTMENTS
-- ---------------------------------------------------------------------------
CREATE TABLE appointments (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    doctor_id           BIGINT NOT NULL,
    appointment_date    DATE NOT NULL,
    appointment_time    TIME NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
    reason              VARCHAR(255),
    notes               VARCHAR(500),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
) ENGINE=InnoDB;

CREATE INDEX idx_appointments_patient_id ON appointments (patient_id);
CREATE INDEX idx_appointments_doctor_id_date ON appointments (doctor_id, appointment_date);

-- ---------------------------------------------------------------------------
-- ADMISSIONS  (in-patient stays)
-- ---------------------------------------------------------------------------
CREATE TABLE admissions (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    bed_id              BIGINT NOT NULL,
    doctor_id           BIGINT NOT NULL,
    admission_date      DATETIME NOT NULL,
    discharge_date      DATETIME,
    admission_reason    VARCHAR(255),
    status              VARCHAR(20) NOT NULL DEFAULT 'ADMITTED', -- ADMITTED, DISCHARGED
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_admissions_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_admissions_bed FOREIGN KEY (bed_id) REFERENCES beds (id),
    CONSTRAINT fk_admissions_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- MEDICAL RECORDS & PRESCRIPTIONS
-- ---------------------------------------------------------------------------
CREATE TABLE medical_records (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    doctor_id       BIGINT NOT NULL,
    appointment_id  BIGINT,
    diagnosis       VARCHAR(500) NOT NULL,
    treatment       VARCHAR(500),
    notes           VARCHAR(1000),
    record_date     DATE NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_medrec_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_medrec_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    CONSTRAINT fk_medrec_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id)
) ENGINE=InnoDB;

CREATE TABLE prescriptions (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id   BIGINT NOT NULL,
    medication_name     VARCHAR(150) NOT NULL,
    dosage              VARCHAR(50) NOT NULL,
    frequency           VARCHAR(50) NOT NULL,
    duration_days       INT NOT NULL,
    instructions        VARCHAR(500),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prescriptions_medrec FOREIGN KEY (medical_record_id) REFERENCES medical_records (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------------
-- BILLING
-- ---------------------------------------------------------------------------
CREATE TABLE invoices (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    appointment_id  BIGINT,
    admission_id    BIGINT,
    total_amount    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PAID, CANCELLED
    issued_date     DATE NOT NULL,
    due_date        DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoices_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    CONSTRAINT fk_invoices_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id),
    CONSTRAINT fk_invoices_admission FOREIGN KEY (admission_id) REFERENCES admissions (id)
) ENGINE=InnoDB;

CREATE TABLE invoice_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id      BIGINT NOT NULL,
    description     VARCHAR(255) NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    quantity        INT NOT NULL DEFAULT 1,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id)
) ENGINE=InnoDB;
