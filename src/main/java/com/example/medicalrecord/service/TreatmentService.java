package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreateTreatmentDto;
import com.example.medicalrecord.dto.TreatmentDto;

import java.util.List;

public interface TreatmentService {


    TreatmentDto createTreatment(CreateTreatmentDto dto);

    List<TreatmentDto> getAllTreatments();

    TreatmentDto getTreatmentById(Long id);

    TreatmentDto updateTreatment(Long id, CreateTreatmentDto dto);

    void deleteTreatment(Long id);
}
