package com.hospital.hms.doctor.repository;

import com.hospital.hms.doctor.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor , Long> {
    boolean existsByEmail(String email);
    Page<Doctor> findByDepartmentId(Long departmentId , Pageable pageable);
    Page<Doctor> findBySpecialization(String specialization , Pageable pageable);
}
