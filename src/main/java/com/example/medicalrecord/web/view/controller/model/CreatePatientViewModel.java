package com.example.medicalrecord.web.view.controller.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePatientViewModel {
    private Long id;
    private String patientName;
    private String egn;
    private boolean hasPaidInsuranceLastSixMonths;
    private long personalDoctorId;
    private String username;
}
