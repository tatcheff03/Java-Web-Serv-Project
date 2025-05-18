package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SickLeaveRepository  extends JpaRepository<SickLeave, Long> {

}
