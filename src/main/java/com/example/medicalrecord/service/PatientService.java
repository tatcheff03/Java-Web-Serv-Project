package com.example.medicalrecord.service;

import com.example.medicalrecord.data.entity.Patient;
import com.example.medicalrecord.dto.CreatePatientDto;
import com.example.medicalrecord.dto.PatientDto;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    PatientDto createPatient(CreatePatientDto createPatientDto);
    List<PatientDto> getPatientsByDoctorId(Long doctorId);
    PatientDto getPatientById(Long id);
    PatientDto updatePatient(Long id, CreatePatientDto createPatientDto);
    void deletePatient(Long id);
    Optional<Patient> findByUsername(String username);
    List<PatientDto> getAllDeletedPatients();
    List<PatientDto> getAllActivePatients();
    void restorePatient(Long id);


}
