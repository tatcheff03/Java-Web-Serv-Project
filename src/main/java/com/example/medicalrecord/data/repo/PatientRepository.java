package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository  extends JpaRepository<Patient, Long> {
}
