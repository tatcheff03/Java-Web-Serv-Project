package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.*;

import javax.print.Doc;
import java.time.LocalDate;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sick_leave_id")
    private SickLeave sickLeave;
}
