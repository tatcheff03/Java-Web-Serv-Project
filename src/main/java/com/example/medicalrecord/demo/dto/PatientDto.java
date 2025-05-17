package com.example.medicalrecord.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientDto {
    private Long Id;
    private  String name;
    private long EGN;
    private boolean hasPaidInsuranceLastSixMonths;
    private DoctorDto personalDoctor;
}
