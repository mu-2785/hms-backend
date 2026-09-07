package com.hospital.hms.doctor.dto;



import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DoctorResponseDto {



    private Long id;
    private String firstName;

    private String lastName;

    private String email;
    private String phone;

    private String specialization;

    private Long departmentId;   //  foreign key
    private String departmentName;
    private BigDecimal consultationFee;

    private Integer yearsOfExperience;


}
