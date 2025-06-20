package com.example.medicalrecord.web.view.controller.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class CreateSickLeaveViewModel {

    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @Min(value = 1, message = "Duration must be at least 1 day")
    @Max(value = 365 , message = "Duration cannot exceed 365 days")
    private int dayDuration;

    @NotNull(message = "Patient must be selected")
    private Long patientId;

    @NotNull(message = "Doctor is required")
    private Long issuedById;
}
