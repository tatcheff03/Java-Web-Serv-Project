package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

}
