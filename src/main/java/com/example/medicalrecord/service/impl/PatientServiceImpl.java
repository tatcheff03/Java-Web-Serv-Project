package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.util.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.medicalrecord.data.entity.Patient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final VisitRepository visitRepository;
    private final MapperUtil mapperUtil;

    @Override
    public PatientDto createPatient(CreatePatientDto createpatientDto) {
        Doctor doctor = doctorRepository.findById(createpatientDto.getPersonalDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = mapperUtil.map(createpatientDto, Patient.class);
        patient.setId(null);
        patient.setPatientName(createpatientDto.getPatientName());
        patient.setEgn(createpatientDto.getEgn());
        patient.setPersonalDoctor(doctor);
        patient.setUsername(createpatientDto.getUsername());
        Patient savedPatient = patientRepository.save(patient);
        return mapperUtil.map(savedPatient, PatientDto.class);

    }





    @Override
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
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
    public Optional<Patient> findByUsername(String username) {
        return patientRepository.findByUsername(username);
    }


    @Override
    public List<PatientDto> getPatientsByDoctorId(Long doctorId) {
        return patientRepository.findByPersonalDoctorId(doctorId)
                .stream()
                .map(p -> mapperUtil.map(p, PatientDto.class))
                .toList();
    }



    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        boolean hasDependencies =
                !visitRepository.findAllByPatientIdAndDeletedFalse(id).isEmpty()
                        || !sickLeaveRepository.findAllByPatientIdAndDeletedFalse(id).isEmpty();

        if (hasDependencies) {

            patient.setDeleted(true);
            patientRepository.save(patient);
        } else {

            patientRepository.delete(patient);
        }
    }




    @Override
    public void restorePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setDeleted(false);
        patientRepository.save(patient);
    }



    @Override
    public List<PatientDto> getAllActivePatients() {
        List<Patient> patients = patientRepository.findAllByDeletedFalse();

        System.out.println("All active patients = " + patients.size());
        patients.forEach(p -> System.out.println(p.getPatientName()));

        return patients.stream()
                .map(p -> mapperUtil.map(p, PatientDto.class))
                .toList();
    }




    @Override
    public List<PatientDto> getAllDeletedPatients() {
        return patientRepository.findAllByDeletedTrue()
                .stream()
                .map(p -> mapperUtil.map(p, PatientDto.class))
                .toList();
    }



}