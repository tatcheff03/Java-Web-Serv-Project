package com.example.medicalrecord.web.view.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;



import java.time.LocalDate;

@Getter
@Setter
public class CreateVisitViewModel {
    private Long id;
    @NotNull(message = "Doctor must be selected")
    private Long doctorId;
    @NotNull(message = "Patient must be selected")
    private Long patientId;
    @NotNull(message = "Date must be provided")
    @PastOrPresent(message = "Date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate localDate;
    private Long diagnosisId;
    private Long treatmentId;
    private Long sickLeaveId;

}
