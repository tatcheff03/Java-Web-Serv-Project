package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMedicineDto
{
    private String medicineName;
    private String dosage;
}
