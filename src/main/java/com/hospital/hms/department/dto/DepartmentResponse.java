package com.hospital.hms.department.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public record DepartmentResponse (

    Long id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){}
