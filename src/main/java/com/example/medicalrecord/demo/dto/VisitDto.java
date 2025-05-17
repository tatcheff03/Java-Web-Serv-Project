package com.example.medicalrecord.demo.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VisitDto {
    private Long id;
    private DoctorDto doctor;
    private PatientDto patient;
    private LocalDate localDate;
    private DiagnosisDto diagnosis;
    private TreatmentDto treatment;
    private SickLeaveDto sickLeave;
}
