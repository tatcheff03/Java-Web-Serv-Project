package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePatientDto {
    @Size(min=3, max=100)
    private String patientName;
    @Pattern(regexp = "^[0-9]{10}$", message = "EGN must be exactly 10 digits")
    private String egn;
    private boolean hasPaidInsuranceLastSixMonths;
    @NotNull(message = "Personal doctor ID cannot be null")
    private long personalDoctorId;
    @NotBlank(message = "Username cannot be blank")
    private String username;
}
