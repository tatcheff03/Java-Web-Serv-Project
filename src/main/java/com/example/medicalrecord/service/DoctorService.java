package com.example.medicalrecord.service;

import com.example.medicalrecord.dto.CreateDoctorDto;
import com.example.medicalrecord.dto.DoctorDto;

import java.util.List;

public interface DoctorService {
    DoctorDto createDoctor(CreateDoctorDto createDoctorDto);
    List<DoctorDto> getAllDoctors();
    DoctorDto getDoctorById(Long id);
    DoctorDto updateDoctor(Long id, CreateDoctorDto createDoctorDto);
    void deleteDoctor(Long id);
}
