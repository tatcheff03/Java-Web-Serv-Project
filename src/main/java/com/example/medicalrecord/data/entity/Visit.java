package com.example.medicalrecord.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Visit extends BaseEntity {

    @ManyToOne
    @NotNull
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @NotNull
    @PastOrPresent
    private LocalDate localDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "sick_leave_id")
    private SickLeave sickLeave;

    @Column(nullable = false)
    private boolean deleted = false;
}
