package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.entity.Diagnosis;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.service.VisitService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.SickLeaveViewModel;
import com.example.medicalrecord.web.view.controller.model.VisitViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.medicalrecord.util.OwnershipUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final MapperUtil mapperUtil;
    private final MedicineRepository medicineRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final TreatmentRepository treatmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final OwnershipUtil ownershipUtil;


    @Override
    public VisitDto createVisit(CreateVisitDto dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));

        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId()).orElseThrow(() -> new RuntimeException("Diagnosis not found"));


        Visit visit = new Visit();
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visit.setDiagnosis(diagnosis);
        visit.setLocalDate(dto.getLocalDate());

        if (dto.getTreatmentId() != null) {
            Treatment treatment = treatmentRepository.findById(dto.getTreatmentId()).orElseThrow(() -> new RuntimeException("Treatment not found"));
            visit.setTreatment(treatment);
        }

        if (dto.getSickLeaveId() != null) {
            SickLeave sickLeave = sickLeaveRepository.findById(dto.getSickLeaveId()).orElseThrow(() -> new RuntimeException("SickLeave not found"));
            visit.setSickLeave(sickLeave);
        }

        Visit saved = visitRepository.save(visit);
        System.out.println("✔ Visit saved with doctorId = " + dto.getDoctorId());
        return mapperUtil.map(saved, VisitDto.class);
    }

    @Override
    public List<VisitDto> getAllVisits() {
        return visitRepository.findAllByDeletedFalse().stream().map(v -> mapperUtil.map(v, VisitDto.class)).collect(Collectors.toList());
    }

    @Override
    public VisitDto getVisitById(Long id) {
        Visit visit = visitRepository.findById(id).orElseThrow(() -> new RuntimeException("Visit not found"));
        return mapperUtil.map(visit, VisitDto.class);
    }

    @Override
    public VisitDto updateVisit(Long id, UpdateVisitDto dto) {
        Visit visit = visitRepository.findById(id).orElseThrow(() -> new RuntimeException("Visit not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor not found"));
        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId()).orElseThrow(() -> new RuntimeException("Diagnosis not found"));

        visit.setDoctor(doctor);
        visit.setDiagnosis(diagnosis);


        if (dto.getTreatmentId() != null) {
            Treatment treatment = treatmentRepository.findById(dto.getTreatmentId()).orElseThrow(() -> new RuntimeException("Treatment not found"));
            visit.setTreatment(treatment);
        } else {
            visit.setTreatment(null);
        }

        if (dto.getSickLeaveId() != null) {
            SickLeave sickLeave = sickLeaveRepository.findById(dto.getSickLeaveId()).orElseThrow(() -> new RuntimeException("SickLeave not found"));
            visit.setSickLeave(sickLeave);
        } else {
            visit.setSickLeave(null);
        }

        if (dto.getLocalDate() != null) {
            visit.setLocalDate(dto.getLocalDate());
        }

        Visit updated = visitRepository.save(visit);
        return mapperUtil.map(updated, VisitDto.class);
    }

    @Override
    public void deleteVisit(Long id) {
        Visit visit = visitRepository.findById(id).orElseThrow(() -> new RuntimeException("Visit not found"));

        if (visit.getTreatment() != null || visit.getSickLeave() != null) {
            visit.setDeleted(true);
            visitRepository.save(visit);
            throw new SoftDeleteException("Cannot hard-delete: visit has linked records and was soft-deleted instead.");
        }
        visitRepository.deleteById(id);
    }

    @Override
    public VisitDto addTreatment(Long visitId, CreateTreatmentDto dto) {
        Visit visit = visitRepository.findById(visitId).orElseThrow();

        Treatment treatment = mapperUtil.map(dto, Treatment.class);

        Set<Medicine> meds = dto.getMedicationIds().stream().map(id -> medicineRepository.findById(id).orElseThrow()).collect(Collectors.toSet());

        treatment.setMedications(meds);
        visit.setTreatment(treatment);

        return mapperUtil.map(visitRepository.save(visit), VisitDto.class);
    }

    @Override
    public VisitDto addSickLeave(Long visitId, CreateSickLeaveDto dto) {
        Visit visit = visitRepository.findById(visitId).orElseThrow(() -> new RuntimeException("Visit not found"));

        SickLeave sickLeave;

        if (visit.getSickLeave() != null) {

            sickLeave = visit.getSickLeave();
        } else {

            sickLeave = new SickLeave();
        }

        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setDayDuration(dto.getDayDuration());

        Doctor issuedBy = doctorRepository.findById(dto.getIssuedById()).orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));

        sickLeave.setIssuedBy(issuedBy);
        sickLeave.setPatient(patient);

        visit.setSickLeave(sickLeave);
        visitRepository.save(visit);

        return mapperUtil.map(visit, VisitDto.class);
    }

    @Override
    public VisitDto attachSickLeave(Long visitId, Long sickLeaveId) {
        Visit visit = visitRepository.findById(visitId).orElseThrow(() -> new RuntimeException("Visit not found"));

        SickLeave sickLeave = sickLeaveRepository.findById(sickLeaveId).orElseThrow(() -> new RuntimeException("SickLeave not found"));

        visit.setSickLeave(sickLeave);
        return mapperUtil.map(visitRepository.save(visit), VisitDto.class);
    }

    @Override
    public VisitDto attachTreatment(Long visitId, Long treatmentId) {
        Visit visit = visitRepository.findById(visitId).orElseThrow(() -> new RuntimeException("Visit not found"));

        Treatment treatment = treatmentRepository.findById(treatmentId).orElseThrow(() -> new RuntimeException("Treatment not found"));

        visit.setTreatment(treatment);

        return mapperUtil.map(visitRepository.save(visit), VisitDto.class);
    }

    @Override
    public List<VisitDto> getAllDeletedVisits() {
        return visitRepository.findAllByDeletedTrue().stream().map(v -> mapperUtil.map(v, VisitDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<VisitViewModel> getVisitsByPatientId(Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {

            String username = auth.getName();
            Long currentDoctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Doctor not found")).getId();

            PatientDto patientDto = patientService.getPatientById(patientId);
            Long personalDoctorId = patientDto.getPersonalDoctor().getId();

            OwnershipUtil.verifyOwnership(personalDoctorId, currentDoctorId, "❌ Нямате достъп до този пациент");
        }

        return visitRepository.findAllByPatientIdAndDeletedFalse(patientId)
                .stream()
                .map(visit -> {
                    VisitViewModel vm = mapperUtil.map(visit, VisitViewModel.class);
                    vm.setDoctor(mapperUtil.map(visit.getDoctor(), DoctorDto.class));
                    vm.setPatient(mapperUtil.map(visit.getPatient(), PatientDto.class));
                    vm.setLocalDate(visit.getLocalDate());
                    if (visit.getSickLeave() != null) {
                        vm.setSickLeave(mapperUtil.map(visit.getSickLeave(), SickLeaveViewModel.class));
                    }
                    if (visit.getDiagnosis() != null) {
                        vm.setDiagnosis(mapperUtil.map(visit.getDiagnosis(), DiagnosisDto.class));
                    }
                    if (visit.getTreatment() != null) {
                        vm.setTreatment(mapperUtil.map(visit.getTreatment(), TreatmentDto.class));
                    }

                    return vm;
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<VisitViewModel> getVisitsBetweenDates(LocalDate start, LocalDate end) {
        return visitRepository.findAllByLocalDateBetweenAndDeletedFalse(start, end)
                .stream()
                .map(visit -> {
                    VisitViewModel vm = mapperUtil.map(visit, VisitViewModel.class);
                    vm.setDoctor(mapperUtil.map(visit.getDoctor(), DoctorDto.class));
                    vm.setPatient(mapperUtil.map(visit.getPatient(), PatientDto.class));
                    vm.setLocalDate(visit.getLocalDate());

                    if (visit.getDiagnosis() != null) {
                        vm.setDiagnosis(mapperUtil.map(visit.getDiagnosis(), DiagnosisDto.class));
                    }
                    if (visit.getTreatment() != null) {
                        vm.setTreatment(mapperUtil.map(visit.getTreatment(), TreatmentDto.class));
                    }
                    if (visit.getSickLeave() != null) {
                        vm.setSickLeave(mapperUtil.map(visit.getSickLeave(), SickLeaveViewModel.class));
                    }

                    return vm;
                })
                .collect(Collectors.toList());
    }



    @Override
    public Map<String, Long> getVisitCountPerDoctor() {
        return visitRepository.findAll().stream().filter(v -> !v.getDoctor().isDeleted()).collect(Collectors.groupingBy(v -> v.getDoctor().getDoctorName(), Collectors.counting()));
    }

    @Override
    public List<VisitViewModel> getVisitsByDoctorAndPeriod(Long doctorId, LocalDate start, LocalDate end) {
        return visitRepository.findAllByDoctorIdAndLocalDateBetweenAndDeletedFalse(doctorId, start, end)
                .stream()
                .map(visit -> {
                    VisitViewModel vm = mapperUtil.map(visit, VisitViewModel.class);
                    vm.setDoctor(mapperUtil.map(visit.getDoctor(), DoctorDto.class));
                    vm.setPatient(mapperUtil.map(visit.getPatient(), PatientDto.class));
                    vm.setLocalDate(visit.getLocalDate());

                    if (visit.getDiagnosis() != null) {
                        vm.setDiagnosis(mapperUtil.map(visit.getDiagnosis(), DiagnosisDto.class));
                    }
                    if (visit.getTreatment() != null) {
                        vm.setTreatment(mapperUtil.map(visit.getTreatment(), TreatmentDto.class));
                    }
                    if (visit.getSickLeave() != null) {
                        vm.setSickLeave(mapperUtil.map(visit.getSickLeave(), SickLeaveViewModel.class));
                    }

                    return vm;
                })
                .collect(Collectors.toList());
    }



    @Override
    public void restoreVisit(Long id) {
        Visit visit = visitRepository.findById(id).orElseThrow(() -> new RuntimeException("Visit not found"));
        visit.setDeleted(false);
        visitRepository.save(visit);
    }


}


