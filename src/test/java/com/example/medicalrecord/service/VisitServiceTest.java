package com.example.medicalrecord.service;

import com.example.medicalrecord.data.entity.Visit;
import com.example.medicalrecord.data.repo.VisitRepository;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.dto.PatientDto;
import com.example.medicalrecord.service.impl.VisitServiceImpl;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.VisitViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.when;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class VisitServiceTest {

    @InjectMocks
    private VisitServiceImpl visitService;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private DoctorDto doctorDto;

    @Mock
    private PatientDto patientDto;


    @Mock
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetVisitsByDoctorAndPeriod() {
        Long doctorId = 1L;
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);

        Visit visit = new Visit();
        visit.setLocalDate(LocalDate.of(2024, 6, 10));
        visit.setDeleted(false);

        when(visitRepository.findAllByDoctorIdAndLocalDateBetweenAndDeletedFalse(doctorId, start, end))
                .thenReturn(Collections.singletonList(visit));

        VisitViewModel vm = new VisitViewModel();
        vm.setLocalDate(visit.getLocalDate());
        vm.setDoctor(doctorDto);
        vm.setPatient(patientDto);

        when(mapperUtil.map(any(Visit.class), eq(VisitViewModel.class))).thenReturn(vm);
        when(mapperUtil.map(any(), eq(DoctorDto.class))).thenReturn(doctorDto);
        when(mapperUtil.map(any(), eq(PatientDto.class))).thenReturn(patientDto);

        List<VisitViewModel> result = visitService.getVisitsByDoctorAndPeriod(doctorId, start, end);

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2024, 6, 10), result.getFirst().getLocalDate());
        assertEquals(doctorDto, result.getFirst().getDoctor());
        assertEquals(patientDto, result.getFirst().getPatient());
    }

    @Test
    //администратор, достъпващ прегледа на пациенти
    void testGetVisitsByPatientId_AsAdmin() {
        Long patientId = 1L;

        // Mock-ване на patient-а
        PatientDto mockPatient = new PatientDto();
        mockPatient.setPersonalDoctor(new DoctorDto()); // задаване на доктор

        when(patientService.getPatientById(patientId)).thenReturn(mockPatient);

        //  Mock-ване на посещение
        Visit visit = new Visit();
        visit.setLocalDate(LocalDate.of(2025, 6, 1));
        visit.setDeleted(false);

        when(visitRepository.findAllByPatientIdAndDeletedFalse(patientId))
                .thenReturn(Collections.singletonList(visit));

        //  Преобразуване
        VisitViewModel vm = new VisitViewModel();
        vm.setLocalDate(visit.getLocalDate());
        vm.setDoctor(doctorDto);
        vm.setPatient(patientDto);

        when(mapperUtil.map(eq(visit), eq(VisitViewModel.class))).thenReturn(vm);
        when(mapperUtil.map(any(), eq(DoctorDto.class))).thenReturn(doctorDto);
        when(mapperUtil.map(any(), eq(PatientDto.class))).thenReturn(patientDto);


        List<VisitViewModel> result = visitService.getVisitsByPatientId(patientId);


        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2025, 6, 1), result.getFirst().getLocalDate());
        assertEquals(doctorDto, result.getFirst().getDoctor());
        assertEquals(patientDto, result.getFirst().getPatient());
    }
}
