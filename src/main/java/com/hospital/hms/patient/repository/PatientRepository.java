package com.hospital.hms.patient.repository;

import com.hospital.hms.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByEmail(String email);

    Optional<Patient> findByEmail(String email);

    // Simple "search" support: match on first or last name, case-insensitive.
    // For anything beyond this (multi-field filters, sorting combos) prefer
    // JPA Specifications over piling up more derived query methods here —
    // see the ticket backlog for the "advanced search" tickets on other modules.
    Page<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);
}
