package com.example.medicalrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiagnosisCountDto {
    private String diagnosisName;
    private Long count;
}
