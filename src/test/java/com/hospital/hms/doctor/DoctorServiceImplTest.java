package com.hospital.hms.doctor;

import com.hospital.hms.common.exception.DuplicateResourceException;
import com.hospital.hms.common.exception.ResourceNotFoundException;
import com.hospital.hms.department.repository.DepartmentRepository;
import com.hospital.hms.doctor.dto.DoctorRequestDto;
import com.hospital.hms.doctor.mapper.DoctorMapper;
import com.hospital.hms.doctor.repository.DoctorRepository;
import com.hospital.hms.doctor.service.DoctorService;
import com.hospital.hms.doctor.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceImplTest {


    @Mock
    private  DoctorRepository doctorRepository;
    @Mock
    private  DoctorMapper doctorMapper;
    @Mock
    private  DepartmentRepository departmentRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void createDoctor_shouldThrowException_whenEmailAlreadyExists(){
        DoctorRequestDto request = DoctorRequestDto.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john@example.com")
                .phone("9876543210")
                .specialization("Cardiology")
                .departmentId(1L)
                .consultationFee(new BigDecimal("1000.00"))
                .yearsOfExperience(5)
                .build();

        when(doctorRepository.existsByEmail("john@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                ()-> doctorService.createDoctor(request)
        );
        verify(doctorRepository).existsByEmail("john@example.com");


    }

    @Test
    void getDoctorById_notFound(){
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                ()-> doctorService.getDoctorById(999L)
        );
        verify(doctorRepository).findById(999L);

    }
    @Test
    void department_notFound(){

        DoctorRequestDto request = DoctorRequestDto.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john@example.com")
                .phone("9876543210")
                .specialization("Cardiology")
                .departmentId(199L)
                .consultationFee(new BigDecimal("1000.00"))
                .yearsOfExperience(5)
                .build();

        when(departmentRepository.findById(199L) ).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                ()-> doctorService.createDoctor(request)
        );

        verify(departmentRepository).findById(199L);

    }
}
