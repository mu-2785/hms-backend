package com.hospital.hms.doctor.service;


import com.hospital.hms.doctor.dto.DoctorRequestDto;
import com.hospital.hms.doctor.dto.DoctorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    Page<DoctorResponseDto>getAllDoctors(Pageable pageable);
    DoctorResponseDto getDoctorById(Long id);
//    DoctorResponseDto createDoctor(List<DoctorRequestDto> doctorRequestDtoList);
    DoctorResponseDto createDoctor(DoctorRequestDto doctorRequestDto);
    DoctorResponseDto updateDoctorById(Long id , DoctorRequestDto doctorRequestDto);
    void deleteDoctorById(Long id);
    Page<DoctorResponseDto> getDoctorsByDepartment(Long departmentId, Pageable pageable);
    Page<DoctorResponseDto> getDoctorsBySpecialization(String specialization, Pageable pageable);
}
