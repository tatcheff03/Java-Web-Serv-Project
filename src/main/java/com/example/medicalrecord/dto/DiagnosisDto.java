package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiagnosisDto {
    private Long id;
    private  String diagnosisName;
    private String description;
}
