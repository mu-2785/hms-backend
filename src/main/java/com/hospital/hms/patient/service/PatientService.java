package com.hospital.hms.patient.service;

import com.hospital.hms.patient.dto.PatientRequest;
import com.hospital.hms.patient.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    PatientResponse createPatient(PatientRequest request);

    PatientResponse getPatientById(Long id);

    Page<PatientResponse> getAllPatients(Pageable pageable);

    Page<PatientResponse> searchPatientsByName(String name, Pageable pageable);

    PatientResponse updatePatient(Long id, PatientRequest request);

    void deletePatient(Long id);
}
