package com.hospital.hms.doctor.service.impl;


import com.hospital.hms.common.exception.DuplicateResourceException;
import com.hospital.hms.common.exception.ResourceNotFoundException;
import com.hospital.hms.department.entity.Department;
import com.hospital.hms.department.repository.DepartmentRepository;
import com.hospital.hms.department.service.DepartmentService;
import com.hospital.hms.doctor.dto.DoctorRequestDto;
import com.hospital.hms.doctor.dto.DoctorResponseDto;
import com.hospital.hms.doctor.entity.Doctor;
import com.hospital.hms.doctor.mapper.DoctorMapper;
import com.hospital.hms.doctor.repository.DoctorRepository;
import com.hospital.hms.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final DepartmentRepository departmentRepository;


    @Override
    public Page<DoctorResponseDto> getAllDoctors(Pageable pageable) {

        return doctorRepository.findAll(pageable).map(doctorMapper::toResponseDto);
    }

    @Override
    public DoctorResponseDto getDoctorById(Long id) {
        return doctorMapper.toResponseDto(getDoctorByIdOrElseThrow(id));
    }

    @Transactional
    @Override
    public DoctorResponseDto createDoctor(DoctorRequestDto doctorRequestDto) {

        Department department = null;
        if(doctorRepository.existsByEmail(doctorRequestDto.getEmail())){
            throw new DuplicateResourceException(String.format("Doctor already exists with email %s",doctorRequestDto.getEmail()));
        }



        Doctor d = doctorMapper.toEntity(doctorRequestDto);
//
//                Doctor.builder()
//                .firstName(doctorRequestDto.getFirstName())
//                .yearsOfExperience(doctorRequestDto.getYearsOfExperience())
//                .consultationFee(doctorRequestDto.getConsultationFee())
//                .specialization(doctorRequestDto.getSpecialization())
//                .email(doctorRequestDto.getEmail())
//                .phone(doctorRequestDto.getPhone())
//                .lastName(doctorRequestDto.getLastName())
//                .build();


        department = departmentRepository.findById(doctorRequestDto.getDepartmentId()).orElseThrow(()-> new ResourceNotFoundException(String.format("Department not found with the Id : %s",doctorRequestDto.getDepartmentId())));

        d.setDepartment(department);

        return doctorMapper.toResponseDto(doctorRepository.save(d));
    }

    @Transactional
    @Override
    public DoctorResponseDto updateDoctorById(Long id, DoctorRequestDto doctorRequestDto) {
        Doctor d = getDoctorByIdOrElseThrow(id);
        if(!d.getEmail().equalsIgnoreCase(doctorRequestDto.getEmail()) && doctorRepository.existsByEmail(doctorRequestDto.getEmail())){
            throw new DuplicateResourceException(String.format("Doctor already exists with email %s",doctorRequestDto.getEmail()));
        }
        doctorMapper.updateFromRequest(d , doctorRequestDto);
        Department department = departmentRepository.findById(doctorRequestDto.getDepartmentId()).orElseThrow(()-> new ResourceNotFoundException(String.format("Department not found by Id : %s" , doctorRequestDto.getDepartmentId())));
        d.setDepartment(department);
        return doctorMapper.toResponseDto(d);
    }

    @Transactional
    @Override
    public void deleteDoctorById(Long id) {
        doctorRepository.delete(getDoctorByIdOrElseThrow(id));
    }

    @Override
    public Page<DoctorResponseDto> getDoctorsByDepartment(Long departmentId, Pageable pageable) {
        return doctorRepository.findByDepartmentId(departmentId , pageable).map(doctorMapper::toResponseDto);
    }

    @Override
    public Page<DoctorResponseDto> getDoctorsBySpecialization(String specialization, Pageable pageable) {
        return doctorRepository.findBySpecialization(specialization, pageable).map(doctorMapper::toResponseDto);
    }


    private Doctor getDoctorByIdOrElseThrow(Long id){
        return doctorRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(String.format("Doctor not found by Id: %s",id)));
    }

}
