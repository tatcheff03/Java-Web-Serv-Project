package com.example.medicalrecord.dto;

import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Getter
@Setter
public class CreateVisitDto {
private Long doctorId;
private Long patientId;
private LocalDate localDate;
private Long diagnosisId;
private Long treatmentId;
private Long sickLeaveId;


}
