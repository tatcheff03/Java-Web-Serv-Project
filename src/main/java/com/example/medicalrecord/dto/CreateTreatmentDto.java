package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateTreatmentDto {
private Set<Long> medicationIds;
private String instructions;
}
