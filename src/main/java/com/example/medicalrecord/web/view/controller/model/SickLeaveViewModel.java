package com.example.medicalrecord.web.view.controller.model;

import lombok.Getter;
import lombok.Setter;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.dto.PatientDto;

import java.time.LocalDate;

@Getter
@Setter
public class SickLeaveViewModel {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private int dayDuration;
    private PatientDto patient;
    private String patientName;
    private String doctorName;
    private DoctorDto issuedBy;
}
