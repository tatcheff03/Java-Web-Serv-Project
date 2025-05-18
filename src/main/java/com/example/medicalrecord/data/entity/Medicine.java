package com.example.medicalrecord.data.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity

public class Medicine extends BaseEntity{
    private  String medicineName;
    private String dosage;
}
