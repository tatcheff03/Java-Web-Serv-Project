package com.example.medicalrecord.web.view.controller.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateTreatmentViewModel {
    private Set<Long> medicationIds;
    private String instructions;
    private Long patientId;
    private  String patientName;
}
