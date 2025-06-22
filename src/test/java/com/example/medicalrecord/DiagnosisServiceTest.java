package com.example.medicalrecord;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.entity.Patient;
import com.example.medicalrecord.dto.DiagnosisDto;
import com.example.medicalrecord.data.entity.Doctor;
import com.example.medicalrecord.data.entity.Diagnosis;
import com.example.medicalrecord.data.repo.DiagnosisRepository;
import com.example.medicalrecord.data.repo.VisitRepository;
import com.example.medicalrecord.service.impl.DiagnosisServiceImpl;
import com.example.medicalrecord.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiagnosisServiceTest {
    @InjectMocks
    private DiagnosisServiceImpl diagnosisService;

    // Mock зависимости
    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private MapperUtil mapperUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    // Връща всички диагнози, които са издадени от конкретен лекар.
    @Test
    void testGetDiagnosisByDoctorId() {
        Long doctorId = 1L;

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(1L);
        diagnosis.setDiagnosisName("Flu");
        diagnosis.setDescription("Seasonal flu");
        diagnosis.setDeleted(false);
        diagnosis.setIssuedBy(new Doctor());
        diagnosis.setPatient(new Patient());

        DiagnosisDto dto = new DiagnosisDto();
        dto.setId(1L);
        dto.setDiagnosisName("Flu");


        when(diagnosisRepository.findByIssuedById
                (doctorId)).thenReturn(List.of(diagnosis));
        when(mapperUtil.map(diagnosis, DiagnosisDto.class)).thenReturn(dto);

        List<DiagnosisDto> result = diagnosisService.getDiagnosesByDoctorId(doctorId);

        assertEquals(1, result.size());
        assertEquals("Flu", result.getFirst().getDiagnosisName());
        assertEquals(1L, result.getFirst().getId());

    }
    // Ако диагнозата е свързана с преглед, се прави soft delete, иначе - hard delete.
    @Test
    void deleteDiagnosis_shouldSoftDeleteIfUsedInVisit() {
        Long id = 1L;
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(id);
        diagnosis.setDeleted(false);

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(diagnosis));
        when(visitRepository.existsByDiagnosisId(id)).thenReturn(true);

        SoftDeleteException thrown = assertThrows(SoftDeleteException.class, () ->
                diagnosisService.deleteDiagnosis(id));

        assertEquals("Cannot hard-delete: diagnosis is linked to visits and was soft-deleted instead.", thrown.getMessage());
        assertTrue(diagnosis.isDeleted());
        verify(diagnosisRepository).save(diagnosis);
    }

    @Test
    void deleteDiagnosis_shouldHardDeleteIfNotUsedInVisit() {
        Long id = 1L;
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(id);

        when(diagnosisRepository.findById(id)).thenReturn(Optional.of(diagnosis));
        when(visitRepository.existsByDiagnosisId(id)).thenReturn(false);

        diagnosisService.deleteDiagnosis(id);

        verify(diagnosisRepository).deleteById(id);
    }

}
