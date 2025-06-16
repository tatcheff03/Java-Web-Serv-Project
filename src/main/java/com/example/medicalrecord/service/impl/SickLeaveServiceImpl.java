package com.example.medicalrecord.service.impl;


import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.SickLeaveService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.Map;
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
        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDayDuration(dto.getDayDuration());


        SickLeave saved = sickLeaveRepository.save(sickLeave);


        SickLeave loaded = sickLeaveRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Sick leave not found after save"));

        SickLeaveDto sickLeaveDto = mapperUtil.map(loaded, SickLeaveDto.class);



        return sickLeaveDto;
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
            throw new SoftDeleteException("Cannot hard-delete: sick leave is linked to a visit and was soft-deleted instead.");
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
    public List<SickLeaveDto> getAllSickLeavesByDoctorId(Long doctorId) {
        return sickLeaveRepository.findByIssuedById(doctorId).stream()
                .map(sickLeave -> mapperUtil.map(sickLeave, SickLeaveDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SickLeaveDto> getAllSickLeavesByPatientId(Long patientId) {
        return sickLeaveRepository.findByPatientId(patientId).stream()
                .map(sickLeave -> mapperUtil.map(sickLeave, SickLeaveDto.class))
                .collect(Collectors.toList());
    }
    public Map.Entry<Month, Long> getMonthWithMostSickLeaves() {
        return sickLeaveRepository.findAllByDeletedFalse().stream()
                .filter(sl -> sl.getStartDate() != null)
                .collect(Collectors.groupingBy(
                        sl -> sl.getStartDate().getMonth(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    @Override
    public Map<Doctor, Long> getDoctorsWithMostSickLeaves() {
        List<SickLeave> sickLeaves = sickLeaveRepository.findAllByDeletedFalse();

        Map<Doctor, Long> countPerDoctor = sickLeaves.stream()
                .filter(sl -> sl.getIssuedBy() != null)
                .collect(Collectors.groupingBy(SickLeave::getIssuedBy, Collectors.counting()));

        long max = countPerDoctor.values().stream().max(Long::compare).orElse(0L);

        return countPerDoctor.entrySet().stream()
                .filter(e -> e.getValue() == max)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }



}


