package com.example.medicalrecord.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Medicine extends BaseEntity{
    @Column(nullable = false)
    @NotBlank
    @Size(min = 2, max = 100)
    private  String medicineName;
    @Column(nullable = false)
    @NotBlank
    @Size(min = 1, max = 30)
    private String dosage;

    @Column(nullable = false)
    private boolean deleted= false;

    @ManyToMany(mappedBy = "medications")
    private Set<Treatment> treatments = new HashSet<>();
}
