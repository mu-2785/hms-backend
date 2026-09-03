package com.hospital.hms.department.service.impl;

import com.hospital.hms.common.exception.DuplicateResourceException;
import com.hospital.hms.common.exception.ResourceNotFoundException;
import com.hospital.hms.department.dto.DepartmentRequest;
import com.hospital.hms.department.dto.DepartmentResponse;
import com.hospital.hms.department.entity.Department;
import com.hospital.hms.department.mapper.DepartmentMapper;
import com.hospital.hms.department.repository.DepartmentRepository;
import com.hospital.hms.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)     //  explain
public class DepartmentServiceImpl implements DepartmentService {


    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable){
        return departmentRepository.findAll(pageable).map(departmentMapper::toResponse);
    }

    private Department findDepartmentOrThrow(Long id){
        return departmentRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(String.format("Department not found by Id: %s",id)));
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id){
         return departmentMapper.toResponse(findDepartmentOrThrow(id));
    }

    @Transactional
    @Override
    public DepartmentResponse createDepartment( DepartmentRequest dR){

        if(departmentRepository.existsByName(dR.name())){
            throw new DuplicateResourceException(String.format("Department with name: %s already exist...",dR.name()));
        }
        return departmentMapper.toResponse(departmentRepository.save(departmentMapper.toEntity(dR)));

    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id , DepartmentRequest dR){
        Department d = findDepartmentOrThrow(id);
        if(!d.getName().equalsIgnoreCase(dR.name()) && departmentRepository.existsByName(dR.name()) ){
            throw new DuplicateResourceException(String.format("Department with name: %s already exist...",dR.name()));
        }

        departmentMapper.updateEntityFromRequest(d , dR);
        return departmentMapper.toResponse(d);
    }

    @Override
    @Transactional
    public void deleteDepartment( Long id){

        departmentRepository.delete(findDepartmentOrThrow(id));

    }


}
