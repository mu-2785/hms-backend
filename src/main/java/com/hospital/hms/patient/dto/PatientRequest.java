package com.hospital.hms.patient.dto;

import com.hospital.hms.patient.entity.BloodGroup;
import com.hospital.hms.patient.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for creating/updating a patient.
 * Kept separate from the entity on purpose: the API contract and the DB
 * model are allowed to evolve independently (e.g. we may never want
 * "createdAt" settable from the client, or may want to rename an entity
 * field without breaking the API).
 */
public record PatientRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Phone number format is invalid")
        String phone,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        Gender gender,

        BloodGroup bloodGroup,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,

        @Size(max = 150, message = "Emergency contact name must not exceed 150 characters")
        String emergencyContactName,

        @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Emergency contact phone format is invalid")
        String emergencyContactPhone
) {
}
