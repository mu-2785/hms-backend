package com.hospital.hms.department.service;

import com.hospital.hms.department.dto.DepartmentRequest;
import com.hospital.hms.department.dto.DepartmentResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DepartmentService {

    Page<DepartmentResponse> getAllDepartments(Pageable pageable);

    DepartmentResponse getDepartmentById(Long id);

    DepartmentResponse createDepartment( DepartmentRequest dR);

    DepartmentResponse updateDepartment(Long id , DepartmentRequest dR);

    // i think  department should not delete until any related record is still active
    void deleteDepartment( Long id);


}
