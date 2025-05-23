package com.example.medicalrecord.web.api;

import com.example.medicalrecord.dto.TreatmentDto;
import com.example.medicalrecord.service.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.medicalrecord.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatments")
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping
    public ResponseEntity<TreatmentDto> createTreatment(@RequestBody CreateTreatmentDto dto) {
        return ResponseEntity.ok(treatmentService.createTreatment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentDto> updateTreatment(@PathVariable Long id, @RequestBody CreateTreatmentDto dto) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<TreatmentDto>> getAll() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreatmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }
}
