package com.example.medicalrecord.web.view.controller.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

import java.time.LocalDate;

@Getter
@Setter
public class CreateVisitViewModel {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private LocalDate localDate;
    private Long diagnosisId;
    private Long treatmentId;
    private Long sickLeaveId;

}
