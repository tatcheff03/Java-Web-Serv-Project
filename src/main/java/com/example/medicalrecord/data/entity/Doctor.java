package com.example.medicalrecord.data.entity;


import com.example.medicalrecord.data.Enum.Specialization;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
public class Doctor extends BaseEntity {

    @Getter

    @NotBlank
    @Size(min = 3, max = 100)
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
    @Size(min = 1, message = "At least one specialization is required")
    private Set<Specialization> specializations = new HashSet<>();

    @NotBlank
    @Size(min = 4, max = 50)
    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private  boolean deleted = false;

    @NotBlank
    @Size(min = 6, max = 20)
    @Column(unique = true, nullable = false)
    private String uniqueIdentifier;

}
