package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository  extends JpaRepository<Medicine, Long>{

}
