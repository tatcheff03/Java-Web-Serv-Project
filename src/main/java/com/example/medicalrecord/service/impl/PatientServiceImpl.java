package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import com.example.medicalrecord.data.entity.Patient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MapperUtil mapperUtil;

    @Override
    public PatientDto createPatient(CreatePatientDto createpatientDto) {
        Doctor doctor = doctorRepository.findById(createpatientDto.getPersonalDoctorId())
                        . orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = mapperUtil.map(createpatientDto, Patient.class);
        patient.setId(null);
        patient.setEgn(createpatientDto.getEgn());
        patient.setPersonalDoctor(doctor);
        patient.setUsername(createpatientDto.getUsername());
        Patient savedPatient = patientRepository.save(patient);
        return mapperUtil.map(savedPatient, PatientDto.class);

    }



    @Override
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patient -> mapperUtil.map(patient, PatientDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PatientDto getPatientById(Long id) {
    Patient patient= patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
    return mapperUtil.map(patient, PatientDto.class);
    }
    @Override
    public PatientDto updatePatient(Long id, CreatePatientDto createPatientDto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(createPatientDto.getPersonalDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        patient.setPatientName(createPatientDto.getPatientName());
        patient.setEgn(createPatientDto.getEgn());
        patient.setHasPaidInsuranceLastSixMonths(createPatientDto.isHasPaidInsuranceLastSixMonths());
        patient.setPersonalDoctor(doctor);
        patient.setUsername(createPatientDto.getUsername());


        Patient updated = patientRepository.save(patient);
        return mapperUtil.map(updated, PatientDto.class);
    }

    @Override
    public Patient findByUsername(String username) {
        return patientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }


    @Override
    public void deletePatient(Long id) {
    if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found");
        }
        patientRepository.deleteById(id);
    }
}
