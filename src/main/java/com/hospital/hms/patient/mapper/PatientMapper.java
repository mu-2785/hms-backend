package com.hospital.hms.patient.mapper;

import com.hospital.hms.patient.dto.PatientRequest;
import com.hospital.hms.patient.dto.PatientResponse;
import com.hospital.hms.patient.entity.Patient;
import org.springframework.stereotype.Component;

/**
 * Manual mapper — deliberately not using MapStruct/ModelMapper here so the
 * mapping logic is explicit and easy to debug while the team is still small.
 * If/when mapping code gets repetitive across many modules, that's a good
 * signal to introduce MapStruct — track that as a separate tech-debt ticket
 * rather than mixing tools mid-module.
 */
@Component
public class PatientMapper {

    public Patient toEntity(PatientRequest request) {
        return Patient.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .bloodGroup(request.bloodGroup())
                .address(request.address())
                .emergencyContactName(request.emergencyContactName())
                .emergencyContactPhone(request.emergencyContactPhone())
                .build();
    }

    /**
     * Applies request fields onto an existing managed entity (used for updates).
     * Kept separate from toEntity() so we never accidentally overwrite id/
     * createdAt/updatedAt on update.
     */
    public void updateEntityFromRequest(Patient patient, PatientRequest request) {
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setBloodGroup(request.bloodGroup());
        patient.setAddress(request.address());
        patient.setEmergencyContactName(request.emergencyContactName());
        patient.setEmergencyContactPhone(request.emergencyContactPhone());
    }

    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getBloodGroup(),
                patient.getAddress(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
}
