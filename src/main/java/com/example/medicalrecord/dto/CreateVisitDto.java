package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
public class CreateVisitDto {
    @NotNull
private Long doctorId;
    @NotNull
private Long patientId;
    @PastOrPresent
private LocalDate localDate;
    @NotNull(message = "Diagnosis ID cannot be null")
private Long diagnosisId;

private Long treatmentId;

private Long sickLeaveId;


}
