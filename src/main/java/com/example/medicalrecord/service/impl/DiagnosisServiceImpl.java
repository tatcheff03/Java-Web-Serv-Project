package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.DiagnosisService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl  implements DiagnosisService {

private final DiagnosisRepository diagnosisRepository;
private final MapperUtil mapperUtil;

    @Override
    public DiagnosisDto createDiagnosis(CreateDiagnosisDto diagnosisDto) {
        Diagnosis diagnosis = mapperUtil.map(diagnosisDto, Diagnosis.class);
        Diagnosis savedDiagnosis = diagnosisRepository.save(diagnosis);
        return mapperUtil.map(savedDiagnosis, DiagnosisDto.class);
    }

    @Override
    public DiagnosisDto getDiagnosisById(Long id) {
       Diagnosis diagnosis = diagnosisRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Diagnosis not found"));
            return mapperUtil.map(diagnosis, DiagnosisDto.class);
    }

    @Override
    public List<DiagnosisDto> getAllDiagnosis() {
        return mapperUtil.mapList(diagnosisRepository.findAll(), DiagnosisDto.class);
    }

    @Override
    public DiagnosisDto updateDiagnosis(Long id, DiagnosisDto diagnosisDto) {
        Diagnosis existing=diagnosisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis doesn't exist"));
        existing.setDiagnosisName(diagnosisDto.getDiagnosisName());
        existing.setDescription(diagnosisDto.getDescription());

        Diagnosis updated=diagnosisRepository.save(existing);
        return mapperUtil.map(updated, DiagnosisDto.class);
    }

    @Override
    public void deleteDiagnosis(Long id) {
        diagnosisRepository.deleteById(id);

    }
}
