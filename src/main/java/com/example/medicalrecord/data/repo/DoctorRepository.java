package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
