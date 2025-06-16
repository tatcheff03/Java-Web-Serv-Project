package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.MedicineRepository;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.MedicineService;
import com.example.medicalrecord.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl  implements MedicineService {
    private final MedicineRepository medicineRepository;
    private final MapperUtil mapperUtil;

    @Override
    public MedicineDto createMedicine(CreateMedicineDto createMedicineDto) {
        Medicine medicine = mapperUtil.map(createMedicineDto, Medicine.class);
        medicine.setDeleted(false);
        Medicine savedMedicine = medicineRepository.save(medicine);
        System.out.println("Creating medicine:" + savedMedicine.getMedicineName() + " with dosage: " );
        return mapperUtil.map(savedMedicine, MedicineDto.class);
    }

    @Override
    public List<MedicineDto> getAllMedicines() {
        return medicineRepository.findAllByDeletedFalse().stream()
                .map(med -> mapperUtil.map(med, MedicineDto.class)).collect(Collectors.toList());
    }

    @Override
    public MedicineDto getMedicineById(Long id) {
        Medicine med = medicineRepository.findById(id).orElseThrow(() -> new RuntimeException("Medicine not found"));
        return mapperUtil.map(med, MedicineDto.class);
    }

    @Override
    public MedicineDto updateMedicine(MedicineDto dto) {
        Medicine med = medicineRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Medicine doesn't exist"));

        med.setMedicineName(dto.getMedicineName());
        med.setDosage(dto.getDosage());

        Medicine updatedMedicine = medicineRepository.save(med);
        return mapperUtil.map(updatedMedicine, MedicineDto.class);
    }

    @Override
    public void restoreMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        if (!medicine.isDeleted()) {
            throw new IllegalStateException("Medicine is already active");
        }

        medicine.setDeleted(false);
        medicineRepository.save(medicine);
    }

    @Override
    public List<MedicineDto> getAllArchivedMedicines() {
        return medicineRepository.findAllByDeletedTrue()
                .stream()
                .map(med -> mapperUtil.map(med, MedicineDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findWithTreatmentsById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        if (!medicine.getTreatments().isEmpty()) {
            medicine.setDeleted(true);
            medicineRepository.save(medicine);
            throw new SoftDeleteException("Cannot hard-delete: medicine has linked treatments and was soft-deleted instead.");
        } else {
            medicineRepository.deleteById(id);
        }




    }
}