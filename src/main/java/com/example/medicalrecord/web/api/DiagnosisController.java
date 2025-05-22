package com.example.medicalrecord.web.api;

import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;


    @PostMapping
    public ResponseEntity<DiagnosisDto> createDiagnosis(@RequestBody CreateDiagnosisDto dto) {
        DiagnosisDto createdDiagnose = diagnosisService.createDiagnosis(dto);
        return ResponseEntity.ok(createdDiagnose);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisDto> getDiagnosisById(@PathVariable Long id) {
        DiagnosisDto diagnosis = diagnosisService.getDiagnosisById(id);
        return ResponseEntity.ok(diagnosis);
    }

    @GetMapping
    public ResponseEntity<List<DiagnosisDto>> getAllDiagnosis() {
        List<DiagnosisDto> diagnoses = diagnosisService.getAllDiagnosis();
        return ResponseEntity.ok(diagnoses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnosisDto> updateDiagnosis(@PathVariable Long id, @RequestBody DiagnosisDto dto) {
        DiagnosisDto updatedDiagnosis = diagnosisService.updateDiagnosis(id, dto);
        return ResponseEntity.ok(updatedDiagnosis);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return ResponseEntity.noContent().build();
    }


}
