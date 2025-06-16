package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.repo.DoctorRepository;
import com.example.medicalrecord.data.repo.MedicineRepository;
import com.example.medicalrecord.data.repo.PatientRepository;
import com.example.medicalrecord.data.repo.TreatmentRepository;
import com.example.medicalrecord.service.TreatmentService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.data.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final MedicineRepository medicineRepository;
    private final DoctorRepository doctorRepository;
    private final MapperUtil mapperUtil;
    private final PatientRepository patientRepository;

    @Override
    public List<TreatmentDto> getAllTreatments() {
        return treatmentRepository.findAll()
                .stream()
                .map(treatment -> mapperUtil.map(treatment, TreatmentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TreatmentDto createTreatment(CreateTreatmentDto dto) {
        Treatment treatment = new Treatment();
        treatment.setInstructions(dto.getInstructions());

        Set<Medicine> medicines = dto.getMedicationIds()
                .stream()
                .map(id -> medicineRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + id)))
                .collect(Collectors.toSet());

        Doctor doctor = doctorRepository.findById(dto.getIssuedById())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        treatment.setMedications(medicines);
        treatment.setIssuedBy(doctor);

        Patient patient= patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        treatment.setPatient(patient);
        Treatment saved = treatmentRepository.save(treatment);
        return mapperUtil.map(saved, TreatmentDto.class);
    }


    @Override
    public TreatmentDto getTreatmentById(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment not found"));

        TreatmentDto dto = mapperUtil.map(treatment, TreatmentDto.class);
        dto.setPatientId(treatment.getPatient() != null ? treatment.getPatient().getId() : null);

        return dto;
    }


    @Override
    public TreatmentDto updateTreatment(Long id, CreateTreatmentDto dto) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment not found"));

        treatment.setInstructions(dto.getInstructions());

        Set<Medicine> medicines = dto.getMedicationIds()
                .stream()
                .map(medId -> medicineRepository.findById(medId)
                        .orElseThrow(() -> new RuntimeException("Medicine not found: " + medId)))
                .collect(Collectors.toSet());

        treatment.setMedications(medicines);

        Treatment updated = treatmentRepository.save(treatment);
        return mapperUtil.map(updated, TreatmentDto.class);
    }

    @Override
    public List<TreatmentDto> getTreatmentsByDoctorId(Long doctorId) {
        return treatmentRepository.findByIssuedById(doctorId)
                .stream()
                .map(treatment -> mapperUtil.map(treatment, TreatmentDto.class))
                .collect(Collectors.toList());
    }



    @Override
    public List<TreatmentDto> getAllDeletedTreatments() {
        return treatmentRepository.findAllByDeletedTrue().stream()
                .map(t -> mapperUtil.map(t, TreatmentDto.class))
                .toList();
    }

    @Override
    public List<TreatmentDto> getAllActiveTreatments() {
        return treatmentRepository.findAllByDeletedFalse()
                .stream()
                .map(t -> mapperUtil.map(t, TreatmentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TreatmentDto> getAllDeletedTreatmentsByDoctorId(Long doctorId) {
        return treatmentRepository.findByIssuedById(doctorId)
                .stream()
                .filter(Treatment::isDeleted)
                .map(t -> mapperUtil.map(t, TreatmentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void restoreTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment not found"));

        treatment.setDeleted(false);
        treatmentRepository.save(treatment);
    }

    @Override
    public void deleteTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment not found"));

        treatment.setDeleted(true);
        treatmentRepository.save(treatment);
        throw new SoftDeleteException("Cannot hard-delete: treatment was soft-deleted instead.");
    }

}
