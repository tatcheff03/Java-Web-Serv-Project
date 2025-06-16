package com.example.medicalrecord.data.repo;

import com.example.medicalrecord.data.entity.Medicine;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository  extends JpaRepository<Medicine, Long>{
List<Medicine>findAllByDeletedTrue();
    List<Medicine> findAllByDeletedFalse();
    @EntityGraph(attributePaths = "treatments")
    Optional<Medicine> findWithTreatmentsById(Long id);
}
