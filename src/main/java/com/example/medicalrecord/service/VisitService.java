package com.example.medicalrecord.service;

import com.example.medicalrecord.data.entity.Visit;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.web.view.controller.model.VisitViewModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VisitService {
    VisitDto createVisit(CreateVisitDto dto);
    List<VisitDto> getAllVisits();
    VisitDto getVisitById(Long id);
    VisitDto updateVisit(Long id, UpdateVisitDto dto);
    void deleteVisit(Long id);
    VisitDto addTreatment(Long visitId, CreateTreatmentDto dto);
    VisitDto addSickLeave(Long visitId, CreateSickLeaveDto dto);
    VisitDto attachSickLeave(Long visitId, Long sickLeaveId);
    VisitDto attachTreatment(Long visitId, Long treatmentId);
    List<VisitDto> getAllDeletedVisits();
    void restoreVisit(Long id);
    List<VisitViewModel> getVisitsByPatientId(Long patientId);
    Map<String,Long>getVisitCountPerDoctor();
    List<VisitViewModel> getVisitsBetweenDates(LocalDate start, LocalDate end);
    List<VisitViewModel> getVisitsByDoctorAndPeriod(Long doctorId, LocalDate start, LocalDate end);



}
