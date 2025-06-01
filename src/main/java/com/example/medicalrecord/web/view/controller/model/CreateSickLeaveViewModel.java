package com.example.medicalrecord.web.view.controller.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateSickLeaveViewModel {
    private Long id;
    private LocalDate startDate;
    private int dayDuration;
    private Long patientId;
    private Long issuedById;
}
