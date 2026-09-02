package com.hospital.hms.patient.dto;

import com.hospital.hms.patient.entity.BloodGroup;
import com.hospital.hms.patient.entity.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        Gender gender,
        BloodGroup bloodGroup,
        String address,
        String emergencyContactName,
        String emergencyContactPhone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
