package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findByIssuedById(Long doctorId);
    List<Diagnosis> findAllByDeletedFalse();
    List<Diagnosis> findAllByDeletedTrue();
    List<Diagnosis> findAllByDiagnosisName(String diagnosisName);

}
