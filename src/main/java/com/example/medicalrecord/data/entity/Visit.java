package com.example.medicalrecord.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Entity
public class Visit extends BaseEntity {

    @ManyToOne
    @JoinColumn (name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private  Patient patient;


    private LocalDate localDate;

    @ManyToOne
    @JoinColumn (name = "diagnosis_id")
    private  Diagnosis diagnosis;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sick_leave_id")
    private SickLeave sickLeave;

    @Column(nullable = false)
    private boolean deleted= false;
}
