package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateTreatmentDto {
    @Size(min = 1, message = "At least one medication must be selected")
private Set<Long> medicationIds;
    @Size(min = 20, max = 200, message = "Instructions must be between 20 and 200 characters")
private String instructions;
private Long issuedById;
    @NotNull(message = "Patient must be selected")
private Long patientId;
}
