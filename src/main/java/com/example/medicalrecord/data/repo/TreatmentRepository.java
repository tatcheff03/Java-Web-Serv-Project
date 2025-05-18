package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository  extends JpaRepository<Treatment, Long> {

}
