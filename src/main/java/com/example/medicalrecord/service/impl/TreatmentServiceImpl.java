package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.data.repo.MedicineRepository;
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
    private final MapperUtil mapperUtil;


    @Override
    public TreatmentDto createTreatment(CreateTreatmentDto dto) {
        Treatment treatment = new Treatment();
        treatment.setInstructions(dto.getInstructions());

        Set<Medicine> medicines = dto.getMedicationIds()
                .stream()
                .map(id -> medicineRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Medicine not found with id: " + id)))
                .collect(Collectors.toSet());

        treatment.setMedications(medicines);

        Treatment saved = treatmentRepository.save(treatment);
        return mapperUtil.map(saved, TreatmentDto.class);
    }

    @Override
    public List<TreatmentDto> getAllTreatments() {
        return treatmentRepository.findAll()
                .stream()
                .map(t -> mapperUtil.map(t, TreatmentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TreatmentDto getTreatmentById(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment not found"));
        return mapperUtil.map(treatment, TreatmentDto.class);
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
    public void deleteTreatment(Long id) {
        if (!treatmentRepository.existsById(id)) {
            throw new RuntimeException("Treatment not found");
        }
        treatmentRepository.deleteById(id);
    }
}
