package com.hospital.hms.department.controller;


import com.hospital.hms.department.dto.DepartmentRequest;
import com.hospital.hms.department.dto.DepartmentResponse;
import com.hospital.hms.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

/*
    POST /api/v1/departments,
    GET /api/v1/departments/{id},
    GET /api/v1/departments (paginated),
    PUT /api/v1/departments/{id},
    DELETE /api/v1/departments/{id}.
*/

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            @PageableDefault(size = 10 , page = 0) Pageable pageable
    ){
        return ResponseEntity.ok( departmentService.getAllDepartments(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id){
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody @Valid DepartmentRequest dR){
        return ResponseEntity.ok(departmentService.createDepartment(dR));
    }

    @PutMapping("{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id , @RequestBody @Valid DepartmentRequest dR){
        return ResponseEntity.ok(departmentService.updateDepartment(id , dR));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id){
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok("deleted");
    }

}
