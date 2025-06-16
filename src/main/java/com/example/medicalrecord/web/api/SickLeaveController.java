package com.example.medicalrecord.web.api;

import com.example.medicalrecord.dto.CreateSickLeaveDto;
import com.example.medicalrecord.dto.SickLeaveDto;
import com.example.medicalrecord.service.SickLeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sickleave")
@RequiredArgsConstructor
public class SickLeaveController {
    private final SickLeaveService sickLeaveService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<SickLeaveDto> create( @Valid @RequestBody CreateSickLeaveDto dto) {
        SickLeaveDto created = sickLeaveService.createSickLeave(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<SickLeaveDto>> getAll() {
        List<SickLeaveDto> sickLeaveList = sickLeaveService.getAllSickLeaves();
        return ResponseEntity.ok(sickLeaveList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<SickLeaveDto> getById(@PathVariable Long id) {
        SickLeaveDto sickLeaveById = sickLeaveService.getSickLeaveById(id);
        return ResponseEntity.ok(sickLeaveById);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<SickLeaveDto> update(@PathVariable Long id,  @Valid@RequestBody CreateSickLeaveDto dto) {
        SickLeaveDto update = sickLeaveService.updateSickLeave(id, dto);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<SickLeaveDto>> getAllDeleted() {
        List<SickLeaveDto> deletedSickLeaves = sickLeaveService.getAllDeletedSickLeaves();
        return ResponseEntity.ok(deletedSickLeaves);
    }

    @PutMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        sickLeaveService.restoreSickLeave(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/by-patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @ResponseBody
    public List<SickLeaveDto> getAllByPatientId(@PathVariable Long patientId) {
        return sickLeaveService.getAllSickLeavesByPatientId(patientId);
    }


}
