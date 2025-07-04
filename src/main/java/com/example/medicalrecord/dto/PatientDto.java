package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientDto {
    private Long id;
    private  String name;
    private String egn;
    private boolean hasPaidInsuranceLastSixMonths;
    private DoctorDto personalDoctor;
    private String username;
}
