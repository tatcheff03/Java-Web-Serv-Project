package com.example.medicalrecord.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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

    @Size(min = 1,message = "At least one medicine is required")
    private Set<Medicine> medications = new HashSet<>();
    @Size(min = 20, max=200 )
    private String instructions;

    @ManyToOne
    private Doctor issuedBy;

    @Column(nullable = false)
    private boolean deleted = false;

    @ManyToOne
    private Patient patient;

}
