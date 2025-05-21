package com.example.medicalrecord.data.entity;


import com.example.medicalrecord.data.Enum.Specialization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
public class Doctor extends BaseEntity {

    private  String doctorName;
    @Column(name = "is_personal_doc")
    private  boolean personalDoc;

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
