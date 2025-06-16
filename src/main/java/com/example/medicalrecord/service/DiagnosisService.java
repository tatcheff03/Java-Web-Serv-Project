package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreateDiagnosisDto;
import com.example.medicalrecord.dto.DiagnosisCountDto;
import com.example.medicalrecord.dto.DiagnosisDto;
import com.example.medicalrecord.dto.PatientDto;

import java.util.List;

public interface DiagnosisService {
    DiagnosisDto createDiagnosis(CreateDiagnosisDto diagnosisDto);
    DiagnosisDto getDiagnosisById(Long id);
    List<DiagnosisDto>getAllDiagnosis();
    DiagnosisDto updateDiagnosis(Long id, CreateDiagnosisDto dto);
    List<DiagnosisDto> getDiagnosesByDoctorId(Long doctorId);
    void deleteDiagnosis(Long id);
    void restoreDiagnosis(Long id);
    List<DiagnosisDto> getAllDeletedDiagnosesByDoctorId(Long doctorId);
    List<DiagnosisDto> getAllDeletedDiagnoses();
    List<DiagnosisDto> getAllActiveDiagnoses();
    List<String> getAllDiagnosisNames();
    List<PatientDto>getPatientsByDiagnosisName(String diagnosisName);
    List<DiagnosisCountDto> getMostCommonDiagnoses();
}
