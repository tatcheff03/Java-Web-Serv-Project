package com.example.medicalrecord.dto;

import com.example.medicalrecord.data.Enum.Specialization;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class CreateDoctorDto {

    private  String doctorName;
    private boolean personalDoc;
    private Set<Specialization> specializations;
}
