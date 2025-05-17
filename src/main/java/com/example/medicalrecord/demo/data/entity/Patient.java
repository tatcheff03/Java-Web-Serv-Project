package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Patient extends BaseEntity {

    private String patientName;
    private long EGN;
    private boolean hasPaidInsuranceLastSixMonths;

    @ManyToOne
    @JoinColumn(name = "personal_doctor_id")
    private Doctor personalDoctor;
}
