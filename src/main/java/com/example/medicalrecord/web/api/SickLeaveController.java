package com.example.medicalrecord.web.api;

import com.example.medicalrecord.dto.CreateSickLeaveDto;
import com.example.medicalrecord.dto.SickLeaveDto;
import com.example.medicalrecord.service.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sickleave")
@RequiredArgsConstructor
public class SickLeaveController {
    private final SickLeaveService sickLeaveService;

    @PostMapping
    public ResponseEntity<SickLeaveDto> create(@RequestBody CreateSickLeaveDto dto) {
        SickLeaveDto created = sickLeaveService.createSickLeave(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<SickLeaveDto>> getAll() {
        List<SickLeaveDto> sickLeaveList = sickLeaveService.getAllSickLeaves();
        return ResponseEntity.ok(sickLeaveList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SickLeaveDto> getById(@PathVariable Long id) {
        SickLeaveDto sickLeaveById = sickLeaveService.getSickLeaveById(id);
        return ResponseEntity.ok(sickLeaveById);

    }

    @PutMapping("/{id}")
    public ResponseEntity<SickLeaveDto> update(@PathVariable Long id, @RequestBody CreateSickLeaveDto dto) {
        SickLeaveDto update = sickLeaveService.updateSickLeave(id, dto);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<SickLeaveDto>> getAllDeleted() {
        List<SickLeaveDto> deletedSickLeaves = sickLeaveService.getAllDeletedSickLeaves();
        return ResponseEntity.ok(deletedSickLeaves);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        sickLeaveService.restoreSickLeave(id);
        return ResponseEntity.noContent().build();
    }

}
