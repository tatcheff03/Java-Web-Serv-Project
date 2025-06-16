package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository  extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUsername(String username);
    List<Patient> findByPersonalDoctorId(Long doctorId);
    List<Patient> findAllByDeletedFalse();
    List<Patient> findAllByDeletedTrue();



}
