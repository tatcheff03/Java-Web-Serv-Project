package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Treatment;
import com.example.medicalrecord.dto.TreatmentDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreatmentRepository  extends JpaRepository<Treatment, Long> {
    List<Treatment> findByIssuedById(Long doctorId);
List<Treatment> findAllByDeletedFalse();
List<Treatment>findAllByDeletedTrue();
boolean existsByPatientId(Long patientId);


}
