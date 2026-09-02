package com.hospital.hms.patient.service.impl;

import com.hospital.hms.common.exception.DuplicateResourceException;
import com.hospital.hms.common.exception.ResourceNotFoundException;
import com.hospital.hms.patient.dto.PatientRequest;
import com.hospital.hms.patient.dto.PatientResponse;
import com.hospital.hms.patient.entity.Patient;
import com.hospital.hms.patient.mapper.PatientMapper;
import com.hospital.hms.patient.repository.PatientRepository;
import com.hospital.hms.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "A patient with email '" + request.email() + "' already exists");
        }

        Patient patient = patientMapper.toEntity(request);
        Patient saved = patientRepository.save(patient);
        return patientMapper.toResponse(saved);
    }

    @Override
    public PatientResponse getPatientById(Long id) {
        Patient patient = findPatientOrThrow(id);
        return patientMapper.toResponse(patient);
    }

    @Override
    public Page<PatientResponse> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable).map(patientMapper::toResponse);
    }

    @Override
    public Page<PatientResponse> searchPatientsByName(String name, Pageable pageable) {
        return patientRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    @Transactional
    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = findPatientOrThrow(id);

        // If the email is changing, make sure it's not colliding with someone else's.
        if (!patient.getEmail().equalsIgnoreCase(request.email())
                && patientRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "A patient with email '" + request.email() + "' already exists");
        }

        patientMapper.updateEntityFromRequest(patient, request);
        // No explicit save() call needed: `patient` is a managed entity inside
        // this @Transactional method, so Hibernate flushes changes automatically
        // (dirty checking) when the transaction commits.
        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = findPatientOrThrow(id);
        patientRepository.delete(patient);
    }

    private Patient findPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Patient", id));
    }
}
