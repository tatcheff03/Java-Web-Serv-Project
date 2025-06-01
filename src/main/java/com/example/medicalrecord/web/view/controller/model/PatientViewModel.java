package com.example.medicalrecord.web.view.controller.model;

import com.example.medicalrecord.dto.DoctorDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientViewModel {
    private Long id;
    private  String name;
    private String egn;
    private boolean hasPaidInsuranceLastSixMonths;
    private DoctorDto personalDoctor;
}
