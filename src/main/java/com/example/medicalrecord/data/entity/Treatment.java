package com.example.medicalrecord.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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
