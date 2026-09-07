package com.hospital.hms.doctor.mapper;


import com.hospital.hms.doctor.dto.DoctorRequestDto;
import com.hospital.hms.doctor.dto.DoctorResponseDto;
import com.hospital.hms.doctor.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public  Doctor toEntity(DoctorRequestDto doctorRequestDto){
        return Doctor.builder()
                .firstName(doctorRequestDto.getFirstName())
                .lastName(doctorRequestDto.getLastName())
                .email(doctorRequestDto.getEmail())
                .yearsOfExperience(doctorRequestDto.getYearsOfExperience())
                .phone(doctorRequestDto.getPhone())
                .consultationFee(doctorRequestDto.getConsultationFee())
                .specialization(doctorRequestDto.getSpecialization())
//                .department(doctorRequestDto.getDepartmentId())
                .build();
    }

    public DoctorResponseDto toResponseDto(Doctor doctor){
        return DoctorResponseDto.builder()
                .id(doctor.getId())
                .consultationFee(doctor.getConsultationFee())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .specialization(doctor.getSpecialization())
                .phone(doctor.getPhone())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .departmentName(doctor.getDepartment().getName())
                .departmentId(doctor.getDepartment().getId())
                .build();
    }

    public void updateFromRequest(Doctor doctor , DoctorRequestDto doctorRequestDto){
        doctor.setFirstName(doctorRequestDto.getFirstName());
        doctor.setLastName(doctorRequestDto.getLastName());
        doctor.setEmail(doctorRequestDto.getEmail());
        doctor.setYearsOfExperience(doctorRequestDto.getYearsOfExperience());
        doctor.setPhone(doctorRequestDto.getPhone());
        doctor.setConsultationFee(doctorRequestDto.getConsultationFee());
        doctor.setSpecialization(doctorRequestDto.getSpecialization());
//        doctor.setDepartment();
    }

}
