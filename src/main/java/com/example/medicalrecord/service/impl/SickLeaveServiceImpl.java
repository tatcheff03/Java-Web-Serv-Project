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
    private final VisitRepository visitRepository;
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
        return sickLeaveRepository.findAllByDeletedFalse()
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

        Doctor doctor = doctorRepository.findById(dto.getIssuedById())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));


        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDayDuration(dto.getDayDuration());
        sickLeave.setIssuedBy(doctor);

        SickLeave updated = sickLeaveRepository.save(sickLeave);
        return mapperUtil.map(updated, SickLeaveDto.class);
    }

    @Override
    public void deleteSickLeave(Long id) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sick leave with ID " + id + " not found."));

        if (visitRepository.existsBySickLeaveId(id)) {
            sickLeave.setDeleted(true);
            sickLeaveRepository.save(sickLeave);
            throw new RuntimeException("Cannot hard-delete: sick leave is linked to a visit and was soft-deleted instead.");
        }

        sickLeaveRepository.deleteById(id);
    }


    @Override
    public List<SickLeaveDto> getAllDeletedSickLeaves() {
        return sickLeaveRepository.findAllByDeletedTrue()
                .stream()
                .map(s -> mapperUtil.map(s, SickLeaveDto.class))
                .toList();
    }

    @Override
    public void restoreSickLeave(Long id) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SickLeave not found"));
        sickLeave.setDeleted(false);
        sickLeaveRepository.save(sickLeave);
    }
    @Override
    public List<SickLeaveDto> getAllSickLeavesByPatientId(Long patientId) {
        return sickLeaveRepository.findByPatientId(patientId).stream()
                .map(sickLeave -> mapperUtil.map(sickLeave, SickLeaveDto.class))
                .collect(Collectors.toList());
    }


}