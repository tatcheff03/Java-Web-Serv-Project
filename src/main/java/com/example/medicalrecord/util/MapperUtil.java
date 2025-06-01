package com.example.medicalrecord.util;

import com.example.medicalrecord.data.entity.Patient;
import com.example.medicalrecord.data.entity.SickLeave;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperUtil {

    private final ModelMapper modelMapper = new ModelMapper();

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

    public <S, T> T map(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    public <S, D> void map(S source, D destination) {
        modelMapper.map(source, destination);
    }

    @PostConstruct
    public void configure() {

        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true);


        modelMapper.createTypeMap(CreatePatientDto.class, Patient.class);

        modelMapper.createTypeMap(CreateDoctorDto.class, Doctor.class);

        modelMapper.createTypeMap(CreateDiagnosisDto.class, Diagnosis.class)
                .addMappings(mapper -> mapper.skip(Diagnosis::setId));

        modelMapper.createTypeMap(CreateMedicineDto.class, Medicine.class)
                .addMappings(mapper -> mapper.skip(Medicine::setId));

        modelMapper.createTypeMap(CreateTreatmentDto.class, Treatment.class)
                .addMappings(mapper -> mapper.skip(Treatment::setId));

        modelMapper.createTypeMap(CreateSickLeaveDto.class, SickLeave.class);
        if (modelMapper.getTypeMap(CreateSickLeaveDto.class, SickLeave.class) == null) {
            modelMapper.createTypeMap(CreateSickLeaveDto.class, SickLeave.class)
                    .addMappings(mapper -> {
                        mapper.skip(SickLeave::setId);
                        mapper.skip(SickLeave::setPatient);
                        mapper.skip(SickLeave::setIssuedBy);
                    });
        }
        modelMapper.createTypeMap(CreateVisitDto.class, Visit.class);

        modelMapper.createTypeMap(Diagnosis.class, DiagnosisDto.class);
        modelMapper.createTypeMap(DiagnosisDto.class, Diagnosis.class)
                .addMappings(mapper -> mapper.skip(Diagnosis::setId));

        modelMapper.createTypeMap(Patient.class, PatientDto.class);

        modelMapper.createTypeMap(Doctor.class, DoctorDto.class);

        modelMapper.createTypeMap(Treatment.class, TreatmentDto.class);

        modelMapper.createTypeMap(SickLeave.class, SickLeaveDto.class);

        modelMapper.createTypeMap(SickLeaveDto.class, SickLeave.class);




        modelMapper.createTypeMap(VisitDto.class, Visit.class);


    }
}
