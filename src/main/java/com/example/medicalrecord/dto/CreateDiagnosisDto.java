package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateDiagnosisDto {

    private  String diagnosisName;
    private String description;
}
