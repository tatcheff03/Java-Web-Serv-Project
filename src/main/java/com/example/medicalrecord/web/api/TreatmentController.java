package com.example.medicalrecord.web.api;

import com.example.medicalrecord.dto.TreatmentDto;
import com.example.medicalrecord.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.medicalrecord.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatments")
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<TreatmentDto> createTreatment(@Valid @RequestBody CreateTreatmentDto dto) {
        return ResponseEntity.ok(treatmentService.createTreatment(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<TreatmentDto> updateTreatment(@PathVariable Long id, @Valid@RequestBody CreateTreatmentDto dto) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<TreatmentDto>> getAll() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<TreatmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }
}
