package com.example.medicalrecord.web.view.controller.model;

import com.example.medicalrecord.data.Enum.Specialization;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DoctorViewModel {
    private Long id;
    private String doctorName;
    private boolean personalDoc;
    private Set<Specialization> specializations;
    private int patientCount;
    private String username;
    private String uniqueIdentifier;
}
