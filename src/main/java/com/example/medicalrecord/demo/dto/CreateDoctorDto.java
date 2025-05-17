package com.example.medicalrecord.demo.dto;

import com.example.medicalrecord.demo.data.Enum.Specialization;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class CreateDoctorDto {

    private  String name;
    private Set<Specialization> specializations;
}
