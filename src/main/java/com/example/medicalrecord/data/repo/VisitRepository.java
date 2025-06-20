    package com.example.medicalrecord.data.repo;

    import com.example.medicalrecord.data.entity.Visit;
    import org.springframework.data.jpa.repository.EntityGraph;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.time.LocalDate;
    import java.util.List;

    public interface VisitRepository extends JpaRepository<Visit, Long> {
        boolean existsBySickLeaveId(Long sickLeaveId);
        @EntityGraph(attributePaths = {"patient", "doctor", "diagnosis", "treatment", "sickLeave"})
        List<Visit> findAllByDeletedFalse();
        List<Visit> findAllByDeletedTrue();
        //List<Visit> findAllByPatientId(Long patientId);
        boolean existsByDiagnosisId(Long diagnosisId);
        @EntityGraph(attributePaths = {"diagnosis", "doctor", "patient", "treatment", "sickLeave"})
        List<Visit> findAllByPatientIdAndDeletedFalse(Long patientId);
        @EntityGraph(attributePaths = {"diagnosis", "doctor", "patient", "treatment", "sickLeave"})
        List<Visit> findAllByLocalDateBetweenAndDeletedFalse(LocalDate start, LocalDate end);
        @EntityGraph(attributePaths = {"diagnosis", "doctor", "patient", "treatment", "sickLeave"})
        List<Visit> findAllByDoctorIdAndLocalDateBetweenAndDeletedFalse(Long doctorId, LocalDate start, LocalDate end);


    }
