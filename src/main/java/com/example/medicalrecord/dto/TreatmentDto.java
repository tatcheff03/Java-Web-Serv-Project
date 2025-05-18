package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TreatmentDto {
    private Long id;
    private Set<MedicineDto> medications;
    private String instructions;
}
