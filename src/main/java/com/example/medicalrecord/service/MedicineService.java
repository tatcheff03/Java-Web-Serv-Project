package com.example.medicalrecord.service;


import com.example.medicalrecord.dto.CreateMedicineDto;
import com.example.medicalrecord.dto.MedicineDto;

import java.util.List;

public interface MedicineService {
    MedicineDto createMedicine(CreateMedicineDto createMedicineDto);

    List<MedicineDto> getAllMedicines();

    MedicineDto getMedicineById(Long id);

    MedicineDto updateMedicine(Long id, CreateMedicineDto createMedicineDto);

    void deleteMedicine(Long id);
}
