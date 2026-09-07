package com.hospital.hms.doctor.entity;


import com.hospital.hms.common.entity.BaseEntity;
import com.hospital.hms.department.entity.Department;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "department")
@Table(name = "doctors")
public class Doctor extends BaseEntity {

    /*
    CREATE TABLE doctors (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL,
    phone               VARCHAR(20)  NOT NULL,
    specialization      VARCHAR(100) NOT NULL,
    department_id       BIGINT NOT NULL,
    consultation_fee    DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    years_of_experience INT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_doctors_email UNIQUE (email),
    CONSTRAINT fk_doctors_department FOREIGN KEY (department_id) REFERENCES departments (id)
) ENGINE=InnoDB;

CREATE INDEX idx_doctors_department_id ON doctors (department_id);
CREATE INDEX idx_doctors_specialization ON doctors (specialization);
     */

    @Column(name = "first_name" , nullable = false , length = 100)
    private String firstName;
    @Column(name = "last_name" , nullable = false , length = 100)
    private String lastName;
    @Column(name = "email" , nullable = false , length = 150 , unique = true)
    private String email;
    @Column(name = "phone" , nullable = false , length = 20 )
    private String phone;
    @Column(name = "specialization" , nullable = false , length = 100)
    private String specialization;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id" , nullable = false  )
    private Department department;   //  foreign key
    @Column(name = "consultation_fee" , nullable = false , precision = 10 , scale = 2)
    private BigDecimal consultationFee;
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

}
