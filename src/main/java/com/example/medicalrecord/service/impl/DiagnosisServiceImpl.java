package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.DiagnosisService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MapperUtil mapperUtil;
    private final VisitRepository visitRepository;

    @Override
    public DiagnosisDto createDiagnosis(CreateDiagnosisDto diagnosisDto) {
        Diagnosis diagnosis = mapperUtil.map(diagnosisDto, Diagnosis.class);

        if (diagnosisDto.getIssuedById() != null) {
            Doctor issuedBy = doctorRepository.findById(diagnosisDto.getIssuedById())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            diagnosis.setIssuedBy(issuedBy);
        }

        if (diagnosisDto.getPatientId() != null) {
            Patient patient = patientRepository.findById(diagnosisDto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            diagnosis.setPatient(patient);
        }

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
    public List<DiagnosisDto> getDiagnosesByDoctorId(Long doctorId) {
        return diagnosisRepository.findByIssuedById(doctorId).stream()
                .map(d -> mapperUtil.map(d, DiagnosisDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllDiagnosisNames() {
        return diagnosisRepository.findAll().stream()
                .map(Diagnosis::getDiagnosisName)
                .distinct()
                .toList();
    }

    @Override
    public List<PatientDto> getPatientsByDiagnosisName(String diagnosisName) {
        return diagnosisRepository.findAllByDiagnosisName(diagnosisName)
                .stream()
                .filter(d -> d.getPatient() != null && !d.getPatient().isDeleted())
                .map(d -> mapperUtil.map(d.getPatient(), PatientDto.class))
                .distinct()
                .toList();
    }

    @Override
    public List<DiagnosisCountDto> getMostCommonDiagnoses() {
        return diagnosisRepository.findAll().stream()
                .filter(d -> !d.isDeleted())
                .collect(Collectors.groupingBy(Diagnosis::getDiagnosisName, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new DiagnosisCountDto(e.getKey(), e.getValue()))
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount())) // descending
                .toList();
    }


    @Override
    public DiagnosisDto updateDiagnosis(Long id, CreateDiagnosisDto dto) {
        Diagnosis existing = diagnosisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis doesn't exist"));
        existing.setDiagnosisName(dto.getDiagnosisName());
        existing.setDescription(dto.getDescription());


        if (dto.getIssuedById() != null) {
            Doctor doctor = doctorRepository.findById(dto.getIssuedById())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            existing.setIssuedBy(doctor);
        }

        Diagnosis updated = diagnosisRepository.save(existing);
        return mapperUtil.map(updated, DiagnosisDto.class);
    }

    @Override
    public void deleteDiagnosis(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis not found"));

        if (visitRepository.existsByDiagnosisId(id)) {
            diagnosis.setDeleted(true);
            diagnosisRepository.save(diagnosis);
            throw new SoftDeleteException("Cannot hard-delete: diagnosis is linked to visits and was soft-deleted instead.");
        }

        diagnosisRepository.deleteById(id);
    }


    @Override
    public void restoreDiagnosis(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnosis not found"));
        diagnosis.setDeleted(false);
        diagnosisRepository.save(diagnosis);
    }

    @Override
    public List<DiagnosisDto> getAllDeletedDiagnosesByDoctorId(Long doctorId) {
        return diagnosisRepository.findAllByDeletedTrue()
                .stream()
                .filter(d -> d.getIssuedBy().getId().equals(doctorId))
                .map(d -> mapperUtil.map(d, DiagnosisDto.class))
                .toList();
    }

    @Override
    public List<DiagnosisDto> getAllDeletedDiagnoses() {


        return diagnosisRepository.findAllByDeletedTrue()
                .stream()
                .map(d -> mapperUtil.map(d, DiagnosisDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<DiagnosisDto> getAllActiveDiagnoses() {
        return diagnosisRepository.findAllByDeletedFalse()
                .stream()
                .map(d -> mapperUtil.map(d, DiagnosisDto.class))
                .toList();
    }


}
