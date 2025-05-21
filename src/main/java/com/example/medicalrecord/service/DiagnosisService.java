package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreateDiagnosisDto;
import com.example.medicalrecord.dto.DiagnosisDto;
import java.util.List;

public interface DiagnosisService {
    DiagnosisDto createDiagnosis(CreateDiagnosisDto diagnosisDto);
    DiagnosisDto getDiagnosisById(Long id);
    List<DiagnosisDto>getAllDiagnosis();
    DiagnosisDto updateDiagnosis(Long id, DiagnosisDto diagnosisDto);
    void deleteDiagnosis(Long id);
}
