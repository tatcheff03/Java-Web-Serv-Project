package com.example.medicalrecord.service.impl;


import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.SickLeaveService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MapperUtil mapperUtil;


    @Override
    public SickLeaveDto createSickLeave(CreateSickLeaveDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(dto.getIssuedById())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        SickLeave sickLeave = mapperUtil.map(dto, SickLeave.class);
        sickLeave.setId(null);
        sickLeave.setPatient(patient);
        sickLeave.setIssuedBy(doctor);

        SickLeave saved = sickLeaveRepository.save(sickLeave);
        return mapperUtil.map(saved, SickLeaveDto.class);
    }

    @Override
    public List<SickLeaveDto> getAllSickLeaves() {
        return sickLeaveRepository.findAll()
                .stream()
                .map(s -> mapperUtil.map(s, SickLeaveDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public SickLeaveDto getSickLeaveById(Long id) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SickLeave not found"));
        return mapperUtil.map(sickLeave, SickLeaveDto.class);
    }

    @Override
    public SickLeaveDto updateSickLeave(Long id, CreateSickLeaveDto dto) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SickLeave not found"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(dto.getIssuedById())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        mapperUtil.map(dto, sickLeave);
        sickLeave.setPatient(patient);
        sickLeave.setIssuedBy(doctor);

        SickLeave updated = sickLeaveRepository.save(sickLeave);
        return mapperUtil.map(updated, SickLeaveDto.class);
    }

    @Override
    public void deleteSickLeave(Long id) {
        if (!sickLeaveRepository.existsById(id)) {
            throw new RuntimeException("SickLeave not found");
        }
        sickLeaveRepository.deleteById(id);
    }
}
