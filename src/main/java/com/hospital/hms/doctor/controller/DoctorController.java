package com.hospital.hms.doctor.controller;

import com.hospital.hms.doctor.dto.DoctorRequestDto;
import com.hospital.hms.doctor.dto.DoctorResponseDto;
import com.hospital.hms.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /*
    @GetMapping
    public ResponseEntity<Page<DoctorResponseDto>> getAllDoctors(@RequestParam(required = false) Long departmentId,
                                                                 @RequestParam(required = false) String specialization,
                                                                 @PageableDefault(size = 15, sort = "firstName") Pageable pageable) {
        //public ResponseEntity<Page<DoctorResponseDto>> getAllDoctors(){

        System.out.println(" getAllDoctors");
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
//        return null;
    }

     */

    @GetMapping
    public ResponseEntity<Page<DoctorResponseDto>> getAllDoctors(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String specialization,
            @PageableDefault(size = 15, sort = "firstName")
            Pageable pageable) {

        if (departmentId != null) {
            return ResponseEntity.ok(
                    doctorService.getDoctorsByDepartment(
                            departmentId,
                            pageable
                    )
            );
        }

        if (specialization != null && !specialization.isBlank()) {
            return ResponseEntity.ok(
                    doctorService.getDoctorsBySpecialization(
                            specialization,
                            pageable
                    )
            );
        }

        return ResponseEntity.ok(
                doctorService.getAllDoctors(pageable)
        );
    }




    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctorById(@PathVariable Long id){
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDto> createDoctor(@Valid @RequestBody DoctorRequestDto doctorRequestDto){
        return ResponseEntity
                .status(201)
                .body(doctorService.createDoctor(doctorRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctorById(@PathVariable Long id , @Valid @RequestBody DoctorRequestDto doctorRequestDto){
        return ResponseEntity.ok(doctorService.updateDoctorById(id , doctorRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDoctorById(@PathVariable Long id){
        doctorService.deleteDoctorById(id);
        return ResponseEntity.status(204)
                .body(null);
    }


}
