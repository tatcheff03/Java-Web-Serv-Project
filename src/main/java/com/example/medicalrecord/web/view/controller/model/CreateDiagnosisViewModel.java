package com.example.medicalrecord.web.view.controller.model;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class CreateDiagnosisViewModel {
    private String diagnosisName;
    private String description;
    private Long patientId;
    private String patientName;
}