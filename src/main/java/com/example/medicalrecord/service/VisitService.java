package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreateVisitDto;
import com.example.medicalrecord.dto.VisitDto;

import java.util.List;

public interface VisitService {
    VisitDto createVisit(CreateVisitDto dto);
    List<VisitDto> getAllVisits();
    VisitDto getVisitById(Long id);
    VisitDto updateVisit(Long id, CreateVisitDto dto);
    void deleteVisit(Long id);
}
