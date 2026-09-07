package com.hospital.hms.doctor.dto;

import com.hospital.hms.department.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DoctorRequestDto {

    @NotBlank(message = "First Name can not be blank")
    @Size(max = 100)
    private String firstName;
    @NotBlank(message = "Last Name can not be blank")
    @Size(max = 100)
    private String lastName;
    @NotBlank(message = "Email can not be blank")
    @Size(max = 150)
    @Email
    private String email;
    @NotBlank(message = "Phone can not be blank")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Specialization can not be blank")
    @Size(max = 100)
    private String specialization;
    @NotNull(message = "Department can not be blank")
    private Long departmentId;   //  foreign key
    @NotNull(message = "Consultation Fee can not be blank")
    private BigDecimal consultationFee;

    private Integer yearsOfExperience;

}
