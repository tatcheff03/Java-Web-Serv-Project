package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    boolean existsBySickLeaveId(Long sickLeaveId);

}
