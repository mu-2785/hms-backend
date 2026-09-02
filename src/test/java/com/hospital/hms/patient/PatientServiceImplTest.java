package com.hospital.hms.patient;

import com.hospital.hms.common.exception.DuplicateResourceException;
import com.hospital.hms.common.exception.ResourceNotFoundException;
import com.hospital.hms.patient.dto.PatientRequest;
import com.hospital.hms.patient.dto.PatientResponse;
import com.hospital.hms.patient.entity.Gender;
import com.hospital.hms.patient.entity.Patient;
import com.hospital.hms.patient.mapper.PatientMapper;
import com.hospital.hms.patient.repository.PatientRepository;
import com.hospital.hms.patient.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example of the testing pattern we use for the service layer: mock the
 * repository + mapper, and assert on behaviour, not implementation details.
 * Use this as the template for testing the other modules' services.
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    private PatientRequest validRequest;
    private Patient patientEntity;

    @BeforeEach
    void setUp() {
        validRequest = new PatientRequest(
                "Jane", "Doe", "jane.doe@example.com", "9876543210",
                LocalDate.of(1990, 5, 20), Gender.FEMALE, null,
                "123 Main St", "John Doe", "9876500000"
        );

        patientEntity = Patient.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1990, 5, 20))
                .gender(Gender.FEMALE)
                .build();
    }

    @Test
    void createPatient_savesAndReturnsResponse_whenEmailIsUnique() {
        when(patientRepository.existsByEmail(validRequest.email())).thenReturn(false);
        when(patientMapper.toEntity(validRequest)).thenReturn(patientEntity);
        when(patientRepository.save(patientEntity)).thenReturn(patientEntity);
        when(patientMapper.toResponse(patientEntity)).thenReturn(
                new PatientResponse(1L, "Jane", "Doe", "jane.doe@example.com", "9876543210",
                        LocalDate.of(1990, 5, 20), Gender.FEMALE, null, "123 Main St",
                        "John Doe", "9876500000", null, null));

        PatientResponse response = patientService.createPatient(validRequest);

        assertThat(response.email()).isEqualTo("jane.doe@example.com");
        verify(patientRepository).save(patientEntity);
    }

    @Test
    void createPatient_throwsDuplicateResourceException_whenEmailAlreadyExists() {
        when(patientRepository.existsByEmail(validRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> patientService.createPatient(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(validRequest.email());
    }

    @Test
    void getPatientById_throwsResourceNotFoundException_whenPatientDoesNotExist() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
