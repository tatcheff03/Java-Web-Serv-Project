package com.example.medicalrecord.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePatientDto {
    private String patientName;
    private long EGN;
    private boolean hasPaidInsuranceLastSixMonths;
    private long personalDoctorId;
}
