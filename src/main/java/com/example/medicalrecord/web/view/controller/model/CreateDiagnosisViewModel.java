package com.example.medicalrecord.web.view.controller.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class CreateDiagnosisViewModel {
    @NotBlank(message = "Diagnosis name is required")
    @Size(max = 20, message = "Diagnosis name must not exceed 20 characters")
    private String diagnosisName;
    @NotBlank(message = "Description is required")
    @Size(max = 40, message = "Description must not exceed 40 characters")
    private String description;
    @NotNull(message = "You must select a patient")
    private Long patientId;
    private String patientName;
}