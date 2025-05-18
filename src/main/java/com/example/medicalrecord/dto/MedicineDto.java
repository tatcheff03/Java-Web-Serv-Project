package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineDto {
    private  Long id;
    private String medicineName;
    private String dosage;
}
