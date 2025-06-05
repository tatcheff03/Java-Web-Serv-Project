package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePatientDto {
    private String patientName;
    private String egn;
    private boolean hasPaidInsuranceLastSixMonths;
    private long personalDoctorId;
    private String username;
}
