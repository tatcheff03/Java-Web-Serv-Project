package com.example.medicalrecord.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateSickLeaveDto {

    private LocalDate startDate;
    private  int dayDuration;
    private Long issuedById;
}
