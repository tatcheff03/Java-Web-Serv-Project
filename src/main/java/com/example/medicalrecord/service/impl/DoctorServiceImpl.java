package com.example.medicalrecord.service.impl;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.data.repo.DoctorRepository;
import com.example.medicalrecord.data.repo.PatientRepository;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.util.MapperUtil;
import jakarta.annotation.PostConstruct;
import  org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final MapperUtil mapperUtil;

    @PostConstruct
    public void init() {
        generateMissingDoctorIdentifiers();
    }

    @Override
    public DoctorDto createDoctor(CreateDoctorDto createDoctorDto)
    {Doctor doctor = mapperUtil.map(createDoctorDto, Doctor.class);
        doctor.setUsername(createDoctorDto.getUsername());


        doctor.setUniqueIdentifier("TEMP");  // временно, за да не е null

        Doctor savedDoctor = doctorRepository.save(doctor);


        savedDoctor.setUniqueIdentifier(String.format("DR-%04d", savedDoctor.getId()));
        return mapperUtil.map(doctorRepository.save(savedDoctor), DoctorDto.class);

    }


    @Override
    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAllByDeletedFalse().stream().map(doctor -> {
            DoctorDto dto = mapperUtil.map(doctor, DoctorDto.class);
            dto.setPatientCount(doctor.getPatients().size());
            return dto;
        }).collect(Collectors.toList());
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
    public Optional<DoctorDto> findByUsername(String username) {
        return doctorRepository.findByUsername(username).map(doctor -> mapperUtil.map(doctor, DoctorDto.class));
    }

    @Override
    public void restoreDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctor.setDeleted(false);
        doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorDto> getAllDeletedDoctors() {
        return doctorRepository.findAllByDeletedTrue()
                .stream()
                .map(d -> mapperUtil.map(d, DoctorDto.class))
                .toList();
    }


    @Override
    public List<DoctorDto> getDoctorsWithPatientCount() {
        //  всички активни доктори
        List<DoctorDto> doctorDtos = doctorRepository.findAllByDeletedFalse().stream().map(doc -> mapperUtil.map(doc, DoctorDto.class)).toList();

        //  всички активни пациенти
        List<PatientDto> patients = patientService.getAllActivePatients();

        // Групиране по doctorId и броене
        Map<Long, Long> countMap = patients.stream().filter(p -> p.getPersonalDoctor() != null).collect(Collectors.groupingBy(p -> p.getPersonalDoctor().getId(), Collectors.counting()));


        doctorDtos.forEach(doc -> {
            int count = countMap.getOrDefault(doc.getId(), 0L).intValue();
            doc.setPatientCount(count);
        });

        return doctorDtos;
    }



    @Transactional(noRollbackFor = SoftDeleteException.class)
    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Patient> patients = patientRepository.findByPersonalDoctorId(id);
        if (!patients.isEmpty()) {
            doctor.setDeleted(true);
            doctorRepository.save(doctor);
            throw new SoftDeleteException("Cannot hard-delete: doctor has patients and was soft-deleted instead.");
        }

        doctorRepository.deleteById(id);
    }

    @Override
    public void generateMissingDoctorIdentifiers() {
        List<Doctor> allDoctors = doctorRepository.findAll();
        for (Doctor doctor : allDoctors) {
            if (doctor.getUniqueIdentifier() == null || doctor.getUniqueIdentifier().isBlank()) {
                doctor.setUniqueIdentifier(String.format("DR-%04d", doctor.getId()));
                doctorRepository.save(doctor);
            }
        }
    }

}

