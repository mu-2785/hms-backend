package com.hospital.hms.department.mapper;

import com.hospital.hms.department.dto.DepartmentRequest;
import com.hospital.hms.department.dto.DepartmentResponse;
import com.hospital.hms.department.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequest request){
        return Department.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public DepartmentResponse toResponse(Department d){
        return new DepartmentResponse(
                d.getId(),
                d.getName(),
                d.getDescription(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(Department d , DepartmentRequest request){
        d.setName(request.name());
        d.setDescription(request.description());
    }
}
