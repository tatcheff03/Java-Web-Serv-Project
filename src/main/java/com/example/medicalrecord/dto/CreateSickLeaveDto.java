package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateSickLeaveDto {
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;
    @Positive
    private int dayDuration;
    @NotNull
    private Long patientId;
    private Long issuedById;
}
