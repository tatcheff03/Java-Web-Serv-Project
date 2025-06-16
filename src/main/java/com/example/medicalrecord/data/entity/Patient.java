package com.example.medicalrecord.data.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Patient extends BaseEntity {

    @NotBlank
    @Size(min = 3, max = 100)
    private String patientName;
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "EGN must be exactly 10 digits")
    private String egn;
    private boolean hasPaidInsuranceLastSixMonths;

    @ManyToOne
    @JoinColumn(name = "personal_doctor_id")
    @NotNull
    private Doctor personalDoctor;

    @Column(unique = true)
    @NotBlank
    @Size(min = 4, max = 50)
    private String username;

    @Column(nullable = false)
    private boolean deleted= false;


}
