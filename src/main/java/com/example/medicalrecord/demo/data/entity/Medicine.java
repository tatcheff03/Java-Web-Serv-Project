package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Medicine extends BaseEntity{
    private  String medicineName;
    private String dosage;
}
