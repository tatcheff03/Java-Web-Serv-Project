package com.example.medicalrecord.dto;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class SickLeaveDto {
    private Long id;
    private LocalDate startDate;
    private int dayDuration;
    private PatientDto patient;
    private DoctorDto issuedBy;
}
