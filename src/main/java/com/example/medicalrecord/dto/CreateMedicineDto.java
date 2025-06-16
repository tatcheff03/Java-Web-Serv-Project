package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMedicineDto
{
    @Size(min = 2, max = 100, message = "Medicine name must be 2-100 characters")
    private String medicineName;
    @Size(min = 2, max = 100, message = "Dosage must be between 2-100 characters")
    private String dosage;
}
