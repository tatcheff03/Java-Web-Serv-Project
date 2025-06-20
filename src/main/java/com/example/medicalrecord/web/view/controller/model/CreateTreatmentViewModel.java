package com.example.medicalrecord.web.view.controller.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateTreatmentViewModel {
    @Size(min = 1, message = "Select at least one medication.")
    private Set<Long> medicationIds;
    @NotBlank(message = "Instructions are required.")
    @Size(min = 20, max = 200, message = "Instructions must be between 20 and 200 characters.")
    private String instructions;
    @NotNull(message = "Patient must be selected.")
    private Long patientId;
    private  String patientName;
}
