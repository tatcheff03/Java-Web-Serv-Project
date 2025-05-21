package com.example.medicalrecord.service.impl;

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
        Medicine savedMedicine = medicineRepository.save(medicine);
        return mapperUtil.map(savedMedicine, MedicineDto.class);
    }

    @Override
    public List<MedicineDto> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(med -> mapperUtil.map(med ,MedicineDto.class)).collect(Collectors.toList());
    }

    @Override
    public MedicineDto getMedicineById(Long id) {
    Medicine med = medicineRepository.findById(id).orElseThrow(() -> new RuntimeException("Medicine not found"));
    return mapperUtil.map(med, MedicineDto.class);
    }

    @Override
    public MedicineDto updateMedicine(Long id, CreateMedicineDto createMedicineDto) {
        Medicine med=medicineRepository.findById(id).orElseThrow(() -> new RuntimeException("Medicine doesn't exist"));
        mapperUtil.map(createMedicineDto, med);
        Medicine updatedMedicine = medicineRepository.save(med);
        return mapperUtil.map(updatedMedicine, MedicineDto.class);
    }

    @Override
    public void deleteMedicine(Long id) {
    if (!medicineRepository.existsById(id)) {
        throw new RuntimeException("Medicine not found");
    }
    medicineRepository.deleteById(id);
    }
}