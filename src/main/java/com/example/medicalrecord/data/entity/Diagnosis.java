package com.example.medicalrecord.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Diagnosis extends BaseEntity{

    @NotBlank
    @Size(min=5 , max=255)
    private String diagnosisName;
    @NotBlank
    @Size(min=5, max=255)
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_by_id", nullable = false)
    private Doctor issuedBy;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
}
