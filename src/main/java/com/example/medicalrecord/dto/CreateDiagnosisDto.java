package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateDiagnosisDto {

    @NotBlank(message = "Diagnosis name is required")
    private  String diagnosisName;
    @NotBlank(message = "Description is required")
    private String description;
    @NotNull(message = "Doctor must be selected")
    private Long issuedById;
    @NotNull(message = "Patient ID cannot be null")
    private  Long patientId;
}
