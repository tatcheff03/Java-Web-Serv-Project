package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreatePatientDto;
import com.example.medicalrecord.dto.PatientDto;

import java.util.List;

public interface PatientService {
    PatientDto createPatient(CreatePatientDto createPatientDto);
    List<PatientDto> getAllPatients();
    PatientDto getPatientById(Long id);
    PatientDto updatePatient(Long id, CreatePatientDto createPatientDto);
    void deletePatient(Long id);
}
