package com.hospital.hms.department.entity;


import com.hospital.hms.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "departments"
)
public class Department extends BaseEntity {

    @Column(name = "name" , nullable = false , length = 100 , unique = true)
    private String name;
    @Column(name = "description" , nullable = true , length = 500 )
    private String description;



}
