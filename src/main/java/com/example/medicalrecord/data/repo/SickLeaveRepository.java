package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.SickLeave;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SickLeaveRepository  extends JpaRepository<SickLeave, Long> {

    @EntityGraph(attributePaths = { "issuedBy", "patient"})
    List<SickLeave> findAllByDeletedFalse();
    
    List<SickLeave> findAllByDeletedTrue();
    List<SickLeave> findByPatientId(Long patientId);
    List<SickLeave> findByIssuedById(Long doctorId);
    List<SickLeave>findAllByPatientIdAndDeletedFalse(Long patientId);




}
