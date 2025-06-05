package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreateSickLeaveDto;
import com.example.medicalrecord.dto.CreateTreatmentDto;
import com.example.medicalrecord.dto.CreateVisitDto;
import com.example.medicalrecord.dto.VisitDto;
import com.example.medicalrecord.web.view.controller.model.VisitViewModel;

import java.util.List;

public interface VisitService {
    VisitDto createVisit(CreateVisitDto dto);
    List<VisitDto> getAllVisits();
    VisitDto getVisitById(Long id);
    VisitDto updateVisit(Long id, CreateVisitDto dto);
    void deleteVisit(Long id);

    VisitDto addTreatment(Long visitId, CreateTreatmentDto dto);
    VisitDto addSickLeave(Long visitId, CreateSickLeaveDto dto);
    VisitDto attachSickLeave(Long visitId, Long sickLeaveId);
    VisitDto attachTreatment(Long visitId, Long treatmentId);
    List<VisitDto> getAllDeletedVisits();
    void restoreVisit(Long id);
    List<VisitViewModel> getVisitsByPatientId(Long patientId);


}
