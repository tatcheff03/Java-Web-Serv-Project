package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

import com.example.medicalrecord.data.Enum.Specialization;

import java.util.Set;

@Getter
@Setter
public class DoctorDto {
    private  Long id;
    private String doctorName;
    private boolean personalDoc;
    private int patientCount;
    private Set<Specialization> specializations;
    private String username;
}
