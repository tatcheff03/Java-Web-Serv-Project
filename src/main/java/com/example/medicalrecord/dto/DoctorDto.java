package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DoctorDto {
    private  Long id;
    private String name;
    private Set <String> Specialization;
}
