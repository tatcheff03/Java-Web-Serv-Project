package com.example.medicalrecord.web.api;


import com.example.medicalrecord.dto.CreateDoctorDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor


public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody CreateDoctorDto createDoctorDto) {
        DoctorDto doctor = doctorService.createDoctor(createDoctorDto);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id, @Valid @RequestBody CreateDoctorDto createDoctorDto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, createDoctorDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

}
