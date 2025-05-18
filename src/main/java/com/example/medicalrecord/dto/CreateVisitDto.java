package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

@Getter
@Setter
public class CreateVisitDto {
private Long doctorId;
private Long patientId;
private Local localDate;
private Long diagnosisId;


}
