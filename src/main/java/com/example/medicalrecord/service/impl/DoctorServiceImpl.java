package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.DoctorRepository;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.util.MapperUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final MapperUtil mapperUtil;

    @Override
    public DoctorDto createDoctor(CreateDoctorDto createDoctorDto) {
        Doctor doctor = mapperUtil.map(createDoctorDto, Doctor.class);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return mapperUtil.map(savedDoctor, DoctorDto.class);
    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> mapperUtil.map(doctor, DoctorDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
        return mapperUtil.map(doctor, DoctorDto.class);
    }

    @Override
    public DoctorDto updateDoctor(Long id, CreateDoctorDto createDoctorDto) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor doesn't exist"));
        mapperUtil.map(createDoctorDto, doctor);
        Doctor updated = doctorRepository.save(doctor);
        return mapperUtil.map(updated, DoctorDto.class);
    }

    @Override
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found");
        }
        doctorRepository.deleteById(id);
    }
}

