package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreateSickLeaveDto;
import com.example.medicalrecord.dto.SickLeaveDto;

import java.util.List;

public interface SickLeaveService {
    SickLeaveDto createSickLeave(CreateSickLeaveDto dto);
    List<SickLeaveDto> getAllSickLeaves();
    SickLeaveDto getSickLeaveById(Long id);
    SickLeaveDto updateSickLeave(Long id, CreateSickLeaveDto dto);
    void deleteSickLeave(Long id);
}
