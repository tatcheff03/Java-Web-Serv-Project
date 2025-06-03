package com.example.medicalrecord.web.view.controller.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TreatmentViewModel {
    private Long id;
    private String instructions;
    List<String> medications;
}
