package com.example.medicalrecord.web.api;

import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;




    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<DiagnosisDto> createDiagnosis(@Valid @RequestBody CreateDiagnosisDto dto) {


        DiagnosisDto createdDiagnose = diagnosisService.createDiagnosis(dto);
        return ResponseEntity.ok(createdDiagnose);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<DiagnosisDto> getDiagnosisById(@PathVariable Long id) {
        DiagnosisDto diagnosis = diagnosisService.getDiagnosisById(id);
        return ResponseEntity.ok(diagnosis);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<DiagnosisDto>> getAllDiagnosis() {
        List<DiagnosisDto> diagnoses = diagnosisService.getAllDiagnosis();
        return ResponseEntity.ok(diagnoses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<DiagnosisDto> updateDiagnosis(@PathVariable Long id, @Valid @RequestBody CreateDiagnosisDto dto) {
        DiagnosisDto updatedDiagnosis = diagnosisService.updateDiagnosis(id, dto);
        return ResponseEntity.ok(updatedDiagnosis);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return ResponseEntity.noContent().build();
    }


}
