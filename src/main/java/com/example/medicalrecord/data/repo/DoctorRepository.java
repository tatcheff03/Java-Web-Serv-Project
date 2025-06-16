package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUsername(String username);
    List<Doctor> findAllByDeletedFalse();
    List<Doctor> findAllByDeletedTrue();
    Optional<Doctor> findByUsernameAndDeletedFalse(String username);

}
