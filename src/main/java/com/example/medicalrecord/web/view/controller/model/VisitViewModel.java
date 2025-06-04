package com.example.medicalrecord.web.view.controller.model;

import lombok.Getter;
import lombok.Setter;
import com.example.medicalrecord.dto.*;

import java.time.LocalDate;


@Getter
@Setter
public class VisitViewModel {
    private Long id;
    private DoctorDto doctor;
    private PatientDto patient;
    private LocalDate localDate;
    private DiagnosisDto diagnosis;
    private TreatmentDto treatment;
    private SickLeaveDto sickLeave;
}
