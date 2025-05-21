package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.data.entity.Diagnosis;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.VisitService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final MapperUtil mapperUtil;



    @Override
    public VisitDto createVisit(CreateVisitDto dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                .orElseThrow(() -> new RuntimeException("Diagnosis not found"));

        Visit visit = new Visit();
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setDiagnosis(diagnosis);
        visit.setLocalDate(dto.getLocalDate());

        Visit saved = visitRepository.save(visit);
        return mapperUtil.map(saved, VisitDto.class);
    }

    @Override
    public List<VisitDto> getAllVisits() {
        return visitRepository.findAll()
                .stream()
                .map(v -> mapperUtil.map(v, VisitDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public VisitDto getVisitById(Long id) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit not found"));
        return mapperUtil.map(visit, VisitDto.class);
    }

    @Override
    public VisitDto updateVisit(Long id, CreateVisitDto dto) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                .orElseThrow(() -> new RuntimeException("Diagnosis not found"));

        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setDiagnosis(diagnosis);
        visit.setLocalDate(dto.getLocalDate());

        Visit updated = visitRepository.save(visit);
        return mapperUtil.map(updated, VisitDto.class);
    }

    @Override
    public void deleteVisit(Long id) {
        if (!visitRepository.existsById(id)) {
            throw new RuntimeException("Visit not found");
        }
        visitRepository.deleteById(id);
    }
}
