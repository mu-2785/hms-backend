package com.hospital.hms.department.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


public record DepartmentRequest(

        @NotBlank(message = "Name can not be blank...")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {

}
