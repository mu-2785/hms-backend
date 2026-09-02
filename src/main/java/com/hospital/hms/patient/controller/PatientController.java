package com.hospital.hms.patient.controller;

import com.hospital.hms.patient.dto.PatientRequest;
import com.hospital.hms.patient.dto.PatientResponse;
import com.hospital.hms.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse created = patientService.createPatient(request);
        return ResponseEntity
                .created(URI.create("/api/v1/patients/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {

        Page<PatientResponse> result = (name == null || name.isBlank())
                ? patientService.getAllPatients(pageable)
                : patientService.searchPatientsByName(name, pageable);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
