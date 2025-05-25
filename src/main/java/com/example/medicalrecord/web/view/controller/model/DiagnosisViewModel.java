package com.example.medicalrecord.web.view.controller.model;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class DiagnosisViewModel {
    private Long id;
    private String diagnosisName;
    private String description;
}