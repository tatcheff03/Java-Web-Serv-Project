package com.example.medicalrecord.demo.data.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Treatment extends BaseEntity {

    @ManyToMany
    @JoinTable(name = "treatment_medicines",
            joinColumns = @JoinColumn(name = "treatment_id"),
            inverseJoinColumns = @JoinColumn(name = "medicine_id")
    )

    private Set<Medicine> medications = new HashSet<>();
    private String instructions;
}
