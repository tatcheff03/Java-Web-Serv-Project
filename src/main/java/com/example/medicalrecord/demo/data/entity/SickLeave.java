package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class SickLeave extends BaseEntity{

    private LocalDate startDate;
    private int dayDuration;

    @ManyToOne
    @JoinColumn(name = "issued_by")
    private Doctor issuedBy;
}
