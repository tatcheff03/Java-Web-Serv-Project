package com.example.medicalrecord.demo.data.entity;


import com.example.medicalrecord.demo.data.Enum.Specialization;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Doctor extends BaseEntity {

    private  String doctorName;
    private  boolean isPersonalDoc;

    @OneToMany(mappedBy = "personalDoctor")
    private Set<Patient> patients = new HashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "specialization")
    private Set<Specialization> specializations = new HashSet<>();

}
