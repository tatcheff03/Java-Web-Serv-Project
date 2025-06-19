package com.example.medicalrecord.web.view.controller.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMedicineViewModel {
    @NotBlank(message = "Medicine name is required.")
    @Size(min = 2, max = 100, message = "Medicine name must be between 2 and 100 characters.")
    private String medicineName;
    @NotBlank(message = "Dosage is required.")
    @Size(min = 1, max = 50, message = "Dosage must be between 1 and 50 characters.")
    private String dosage;
}
