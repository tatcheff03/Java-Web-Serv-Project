package com.example.medicalrecord.web.api;
import com.example.medicalrecord.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.medicalrecord.service.VisitService;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<VisitDto> createVisit(@Valid  @RequestBody CreateVisitDto dto) {
        VisitDto createdVisit = visitService.createVisit(dto);
        return ResponseEntity.ok(createdVisit);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<VisitDto> updateVisit(@PathVariable Long id, @Valid @RequestBody UpdateVisitDto dto) {
        VisitDto updatedVisit = visitService.updateVisit(id, dto);
        return ResponseEntity.ok(updatedVisit);
    }

    @PutMapping("/{id}/treatment")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<VisitDto> addTreatment(@PathVariable Long id, @Valid @RequestBody CreateTreatmentDto dto) {
        return ResponseEntity.ok(visitService.addTreatment(id, dto));
    }

    @PutMapping("/{id}/sick-leave")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<VisitDto> addSickLeave(@PathVariable Long id, @Valid @RequestBody CreateSickLeaveDto dto) {
        return ResponseEntity.ok(visitService.addSickLeave(id, dto));
    }

    @PutMapping("/{id}/sick-leave/attach")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<VisitDto> attachSickLeave(@PathVariable Long id, @Valid @RequestBody AttachSickLeaveDto dto) {
        return ResponseEntity.ok(visitService.attachSickLeave(id, dto.getSickLeaveId()));
    }

    @PutMapping("/{id}/treatment/attach")
@PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<VisitDto> attachTreatment(@PathVariable Long id, @Valid @RequestBody AttachTreatmentDto dto) {
        return ResponseEntity.ok(visitService.attachTreatment(id, dto.getTreatmentId()));
    }




    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<VisitDto>> getAll() {
        return ResponseEntity.ok(visitService.getAllVisits());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<VisitDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(visitService.getVisitById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/deleted")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<VisitDto>> getAllDeleted() {
        return ResponseEntity.ok(visitService.getAllDeletedVisits());
    }

    @PutMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        visitService.restoreVisit(id);
        return ResponseEntity.noContent().build();
    }
}
