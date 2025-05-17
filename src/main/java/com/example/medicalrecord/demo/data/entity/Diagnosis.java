package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Diagnosis extends BaseEntity{

    private String diagnosisName;
    private String description;
}
