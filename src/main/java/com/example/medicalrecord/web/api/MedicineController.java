package com.example.medicalrecord.web.api;

import com.example.medicalrecord.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.medicalrecord.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<MedicineDto> createMedicine(@Valid  @RequestBody CreateMedicineDto dto) {
        MedicineDto created = medicineService.createMedicine(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<MedicineDto> getMedicineById(@PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<MedicineDto>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<MedicineDto> updateMedicine( @Valid @RequestBody MedicineDto dto){
        MedicineDto updated = medicineService.updateMedicine(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }
}

