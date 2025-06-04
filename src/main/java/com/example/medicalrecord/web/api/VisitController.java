package com.example.medicalrecord.web.api;
import com.example.medicalrecord.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.medicalrecord.service.VisitService;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<VisitDto> createVisit(@RequestBody CreateVisitDto dto) {
        VisitDto createdVisit = visitService.createVisit(dto);
        return ResponseEntity.ok(createdVisit);
    }


    @PutMapping("/{id}")
    public ResponseEntity<VisitDto> updateVisit(@PathVariable Long id, @RequestBody CreateVisitDto dto) {
        VisitDto updatedVisit = visitService.updateVisit(id, dto);
        return ResponseEntity.ok(updatedVisit);
    }

    @PutMapping("/{id}/treatment")
    public ResponseEntity<VisitDto> addTreatment(@PathVariable Long id, @RequestBody CreateTreatmentDto dto) {
        return ResponseEntity.ok(visitService.addTreatment(id, dto));
    }

    @PutMapping("/{id}/sick-leave")
    public ResponseEntity<VisitDto> addSickLeave(@PathVariable Long id, @RequestBody CreateSickLeaveDto dto) {
        return ResponseEntity.ok(visitService.addSickLeave(id, dto));
    }

    @PutMapping("/{id}/sick-leave/attach")
    public ResponseEntity<VisitDto> attachSickLeave(@PathVariable Long id, @RequestBody AttachSickLeaveDto dto) {
        return ResponseEntity.ok(visitService.attachSickLeave(id, dto.getSickLeaveId()));
    }

    @PutMapping("/{id}/treatment/attach")
    public ResponseEntity<VisitDto> attachTreatment(@PathVariable Long id, @RequestBody AttachTreatmentDto dto) {
        return ResponseEntity.ok(visitService.attachTreatment(id, dto.getTreatmentId()));
    }




    @GetMapping
    public ResponseEntity<List<VisitDto>> getAll() {
        return ResponseEntity.ok(visitService.getAllVisits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(visitService.getVisitById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/deleted")
    public ResponseEntity<List<VisitDto>> getAllDeleted() {
        return ResponseEntity.ok(visitService.getAllDeletedVisits());
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        visitService.restoreVisit(id);
        return ResponseEntity.noContent().build();
    }
}
