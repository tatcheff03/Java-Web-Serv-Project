package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class SickLeave extends BaseEntity{

    private LocalDate startDate;
    private int dayDuration;

    @ManyToOne
    @JoinColumn(name = "issued_by")
    private Doctor issuedBy;
}
