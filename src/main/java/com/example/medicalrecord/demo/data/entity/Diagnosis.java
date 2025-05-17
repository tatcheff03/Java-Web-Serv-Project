package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Diagnosis extends BaseEntity{

    private String diagnosisName;
    private String description;
}
