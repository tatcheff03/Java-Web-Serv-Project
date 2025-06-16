package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineDto {

    private Long id;
    @Size(min = 2, max = 100, message = "Medicine name must be between 2 and 100 characters")
    private String medicineName;
    @Size(min = 2, max = 50, message = "Dosage must be between 2 and 50 characters")
    @NotBlank(message = "Dosage cannot be blank")
    private String dosage;

    @Override
    public String toString() {
        return medicineName + (dosage != null ? " (" + dosage + ")" : "");
    }
}