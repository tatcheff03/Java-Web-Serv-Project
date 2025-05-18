package com.example.medicalrecord.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Patient extends BaseEntity {

    private String patientName;
    private long EGN;
    private boolean hasPaidInsuranceLastSixMonths;

    @ManyToOne
    @JoinColumn(name = "personal_doctor_id")
    private Doctor personalDoctor;
}
