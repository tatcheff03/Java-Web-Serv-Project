package com.example.medicalrecord.util;


import com.example.medicalrecord.data.entity.SickLeave;
import com.example.medicalrecord.data.entity.*;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.web.view.controller.model.*;
import jakarta.annotation.PostConstruct;
import com.example.medicalrecord.data.Enum.Specialization;
import org.hibernate.Hibernate;
import org.modelmapper.TypeMap;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapperUtil {

    private final ModelMapper modelMapper = new ModelMapper();

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());
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
                .setImplicitMappingEnabled(false)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true);

        if (modelMapper.getTypeMap(Doctor.class, DoctorDto.class) == null) {
            modelMapper.createTypeMap(Doctor.class, DoctorDto.class)


                    .addMappings(mapper -> {
                        mapper.map(Doctor::getId, DoctorDto::setId);
                        mapper.map(Doctor::getDoctorName, DoctorDto::setDoctorName);
                        mapper.map(Doctor::isPersonalDoc, DoctorDto::setPersonalDoc);
                        mapper.map(Doctor::getSpecializations, DoctorDto::setSpecializations);
                        mapper.map(Doctor::getUsername, DoctorDto::setUsername);
                        mapper.map(Doctor::getUniqueIdentifier, DoctorDto::setUniqueIdentifier);
                    })
                    .setPostConverter(ctx -> {
                        Doctor src = ctx.getSource();
                        DoctorDto dest = ctx.getDestination();
                        dest.setDoctorName(src.getDoctorName());
                        System.out.println("🧪 MANUAL set: " + dest.getDoctorName());

                        return dest;
                    });

            TypeMap<CreateDiagnosisDto, Diagnosis> typeMap = modelMapper.createTypeMap(CreateDiagnosisDto.class, Diagnosis.class);

            typeMap.addMappings(mapper -> {
                mapper.map(CreateDiagnosisDto::getDiagnosisName, Diagnosis::setDiagnosisName);
                mapper.map(CreateDiagnosisDto::getDescription, Diagnosis::setDescription);
                mapper.skip(Diagnosis::setId);
                mapper.skip(Diagnosis::setIssuedBy);

            });


            modelMapper.createTypeMap(CreateMedicineDto.class, Medicine.class)
                    .addMappings(mapper -> {
                        mapper.map(CreateMedicineDto::getMedicineName, Medicine::setMedicineName);
                        mapper.map(CreateMedicineDto::getDosage, Medicine::setDosage);
                    });


            TypeMap<CreateSickLeaveDto, SickLeave> sickLeaveMap = modelMapper.createTypeMap(CreateSickLeaveDto.class, SickLeave.class);

            sickLeaveMap.addMappings(mapper -> {
                mapper.skip(SickLeave::setId);
                mapper.skip(SickLeave::setPatient);
                mapper.skip(SickLeave::setIssuedBy);
            });


            TypeMap<DiagnosisDto, Diagnosis> reverseDiagnosisMap = modelMapper.createTypeMap(DiagnosisDto.class, Diagnosis.class);


            reverseDiagnosisMap.addMappings(mapper -> {
                mapper.skip(Diagnosis::setId);
                mapper.skip(Diagnosis::setIssuedBy);
                mapper.map(DiagnosisDto::getDiagnosisName, Diagnosis::setDiagnosisName);
                mapper.map(DiagnosisDto::getDescription, Diagnosis::setDescription);
            });


            modelMapper.createTypeMap(Doctor.class, DoctorViewModel.class)
                    .addMappings(mapper -> {
                        mapper.map(Doctor::getId, DoctorViewModel::setId);
                        mapper.map(Doctor::getDoctorName, DoctorViewModel::setDoctorName);
                        mapper.map(Doctor::isPersonalDoc, DoctorViewModel::setPersonalDoc);
                        mapper.map(src -> src.getSpecializations() != null ? src.getSpecializations().stream().map(Specialization::name).collect(Collectors.joining(", ")) : "", DoctorViewModel::setSpecializations);
                        mapper.map(Doctor::getUniqueIdentifier, DoctorViewModel::setUniqueIdentifier);
                    });


            modelMapper.createTypeMap(Patient.class, PatientDto.class).addMappings(mapper -> {
                        mapper.map(Patient::getId, PatientDto::setId);
                        mapper.map(Patient::getPatientName, PatientDto::setName);
                        mapper.map(Patient::getUsername, PatientDto::setUsername);
                        mapper.map(Patient::getEgn, PatientDto::setEgn);
                        mapper.map(Patient::isHasPaidInsuranceLastSixMonths, PatientDto::setHasPaidInsuranceLastSixMonths);
                        mapper.map(Patient::getPersonalDoctor, PatientDto::setPersonalDoctor);
                    })
                    .setPostConverter(context -> {
                        Patient source = context.getSource();
                        PatientDto destination = context.getDestination();

                        if (source.getPersonalDoctor() != null && !source.getPersonalDoctor().isDeleted()) {
                            destination.setPersonalDoctor(modelMapper.map(source.getPersonalDoctor(), DoctorDto.class));
                        } else {
                            destination.setPersonalDoctor(null);
                        }

                        destination.setUsername(source.getUsername());

                        return destination;
                    });


            modelMapper.createTypeMap(Treatment.class, TreatmentDto.class).addMappings(mapper -> {
                mapper.map(Treatment::getId, TreatmentDto::setId);
                mapper.map(Treatment::getInstructions, TreatmentDto::setInstructions);
                mapper.map(Treatment::getIssuedBy, TreatmentDto::setIssuedBy);
                mapper.map(src -> src.getPatient().getId(), TreatmentDto::setPatientId);
                mapper.map(Treatment::getPatient, TreatmentDto::setPatient);


            }).setPostConverter(context -> {
                Treatment source = context.getSource();
                TreatmentDto destination = context.getDestination();

                if (source.getMedications() != null) {
                    //връща set от лекарства, извлича се имената на лекарствата,
                    // и накрая получ. List, за frontend визуализация
                    Set<MedicineDto> meds = source.getMedications().stream()
                            .filter(med -> !med.isDeleted()) // визуализират се актив. лекарства
                            .map(med -> {
                                MedicineDto dto = new MedicineDto();
                                dto.setId(med.getId());
                                dto.setMedicineName(med.getMedicineName());
                                dto.setDosage(med.getDosage());
                                return dto;
                            })
                            .collect(Collectors.toSet());
                    destination.setMedications(meds);

                }

                return destination;
            });


            modelMapper.createTypeMap(SickLeaveDto.class, CreateSickLeaveViewModel.class)
                    .addMappings(mapper -> {
                        mapper.map(SickLeaveDto::getStartDate, CreateSickLeaveViewModel::setStartDate);
                        mapper.map(SickLeaveDto::getDayDuration, CreateSickLeaveViewModel::setDayDuration);
                        mapper.map(src -> src.getPatient().getId(), CreateSickLeaveViewModel::setPatientId);
                        mapper.map(src -> src.getIssuedBy().getId(), CreateSickLeaveViewModel::setIssuedById);
                    });

            modelMapper.createTypeMap(CreateSickLeaveViewModel.class, CreateSickLeaveDto.class)
                    .addMappings(mapper -> {
                        mapper.map(CreateSickLeaveViewModel::getIssuedById, CreateSickLeaveDto::setIssuedById);
                        mapper.map(CreateSickLeaveViewModel::getPatientId, CreateSickLeaveDto::setPatientId);
                        mapper.map(CreateSickLeaveViewModel::getIssuedById, CreateSickLeaveDto::setIssuedById);
                        mapper.map(CreateSickLeaveViewModel::getStartDate, CreateSickLeaveDto::setStartDate);
                        mapper.map(CreateSickLeaveViewModel::getDayDuration, CreateSickLeaveDto::setDayDuration);

                    });


            TypeMap<SickLeave, SickLeaveDto> sick = modelMapper.createTypeMap(SickLeave.class, SickLeaveDto.class);
            sick


                    .addMappings(mapper -> {
                        mapper.map(SickLeave::getId, SickLeaveDto::setId);
                        mapper.map(SickLeave::getDayDuration, SickLeaveDto::setDayDuration);
                        mapper.map(SickLeave::getStartDate, SickLeaveDto::setStartDate);
                        mapper.map(SickLeave::getPatient, SickLeaveDto::setPatient);

                    })
                    .setPostConverter(context -> {
                        SickLeave source = context.getSource();
                        SickLeaveDto destination = context.getDestination();
                        if (source.getIssuedBy() != null) {
                            Doctor doctor = source.getIssuedBy();
                            Hibernate.initialize(doctor);
                            doctor = (Doctor) Hibernate.unproxy(doctor);


                            DoctorDto dto = modelMapper.map(doctor, DoctorDto.class);
                            destination.setIssuedBy(dto);




                        }


                        return destination;
                    });


            modelMapper.createTypeMap(PatientDto.class, PatientViewModel.class).addMappings(mapper -> {
                mapper.map(PatientDto::getId, PatientViewModel::setId);
                mapper.map(PatientDto::getName, PatientViewModel::setName);
                mapper.map(PatientDto::getEgn, PatientViewModel::setEgn);
                mapper.map(PatientDto::isHasPaidInsuranceLastSixMonths, PatientViewModel::setHasPaidInsuranceLastSixMonths);
                mapper.map(PatientDto::getPersonalDoctor, PatientViewModel::setPersonalDoctor);
            });

            modelMapper.createTypeMap(PatientDto.class, CreatePatientViewModel.class)
                    .addMappings(mapper -> {
                        mapper.map(PatientDto::getId, CreatePatientViewModel::setId);
                        mapper.map(PatientDto::getName, CreatePatientViewModel::setPatientName);
                        mapper.map(PatientDto::getEgn, CreatePatientViewModel::setEgn);
                        mapper.map(PatientDto::getUsername, CreatePatientViewModel::setUsername);
                        mapper.map(PatientDto::isHasPaidInsuranceLastSixMonths, CreatePatientViewModel::setHasPaidInsuranceLastSixMonths);
                        mapper.map(src -> src.getPersonalDoctor().getId(), CreatePatientViewModel::setPersonalDoctorId);

                    });

            modelMapper.createTypeMap(CreatePatientViewModel.class, CreatePatientDto.class)
                    .addMappings(mapper -> {
                        mapper.map(CreatePatientViewModel::getPatientName, CreatePatientDto::setPatientName);
                        mapper.map(CreatePatientViewModel::getEgn, CreatePatientDto::setEgn);
                        mapper.map(CreatePatientViewModel::isHasPaidInsuranceLastSixMonths, CreatePatientDto::setHasPaidInsuranceLastSixMonths);
                        mapper.map(CreatePatientViewModel::getPersonalDoctorId, CreatePatientDto::setPersonalDoctorId);
                        mapper.map(CreatePatientViewModel::getUsername, CreatePatientDto::setUsername); // ако имаш username
                    });


            modelMapper.createTypeMap(DoctorDto.class, DoctorViewModel.class).addMappings(mapper -> {
                mapper.map(DoctorDto::getDoctorName, DoctorViewModel::setDoctorName);
                mapper.map(DoctorDto::getId, DoctorViewModel::setId);
                mapper.map(DoctorDto::isPersonalDoc, DoctorViewModel::setPersonalDoc);
                mapper.map(src -> src.getSpecializations() != null ? src.getSpecializations().stream().map(Specialization::name).collect(Collectors.joining(", ")) : "", DoctorViewModel::setSpecializations);
                mapper.map(DoctorDto::getPatientCount, DoctorViewModel::setPatientCount);
                mapper.map(DoctorDto::getUniqueIdentifier, DoctorViewModel::setUniqueIdentifier);


            });

            modelMapper.createTypeMap(DoctorDto.class, CreateDoctorViewModel.class)
                    .addMappings(mapper -> {
                        mapper.map(DoctorDto::getId, CreateDoctorViewModel::setId);
                        mapper.map(DoctorDto::getDoctorName, CreateDoctorViewModel::setDoctorName);
                        mapper.map(DoctorDto::isPersonalDoc, CreateDoctorViewModel::setPersonalDoc);
                        mapper.map(DoctorDto::getSpecializations, CreateDoctorViewModel::setSpecializations);
                        mapper.map(DoctorDto::getUsername, CreateDoctorViewModel::setUsername);
                        mapper.map(DoctorDto::getUniqueIdentifier, CreateDoctorViewModel::setUniqueIdentifier);
                    });

            modelMapper.createTypeMap(CreateDoctorDto.class, Doctor.class)
                    .addMappings(mapper -> {
                        mapper.map(CreateDoctorDto::getDoctorName, Doctor::setDoctorName);
                        mapper.map(CreateDoctorDto::isPersonalDoc, Doctor::setPersonalDoc);
                        mapper.map(CreateDoctorDto::getSpecializations, Doctor::setSpecializations);
                        mapper.map(CreateDoctorDto::getUsername, Doctor::setUsername);
                    });

            modelMapper.createTypeMap(CreateDoctorViewModel.class, CreateDoctorDto.class)
                    .addMappings(mapper -> {
                        mapper.map(CreateDoctorViewModel::getDoctorName, CreateDoctorDto::setDoctorName);
                        mapper.map(CreateDoctorViewModel::isPersonalDoc, CreateDoctorDto::setPersonalDoc);
                        mapper.map(CreateDoctorViewModel::getSpecializations, CreateDoctorDto::setSpecializations);
                        mapper.map(CreateDoctorViewModel::getUsername, CreateDoctorDto::setUsername);
                    });

            modelMapper.createTypeMap(DoctorDto.class, Doctor.class).addMappings(mapper -> {
                mapper.map(DoctorDto::getId, Doctor::setId);
                mapper.map(DoctorDto::getDoctorName, Doctor::setDoctorName);
                mapper.map(DoctorDto::isPersonalDoc, Doctor::setPersonalDoc);
                mapper.map(DoctorDto::getSpecializations, Doctor::setSpecializations);
                mapper.map(DoctorDto::getUsername, Doctor::setUsername);
                mapper.map(DoctorDto::getUniqueIdentifier, Doctor::setUniqueIdentifier);
            });


            if (modelMapper.getTypeMap(DiagnosisDto.class, DiagnosisViewModel.class) == null) {
                modelMapper.createTypeMap(DiagnosisDto.class, DiagnosisViewModel.class).addMappings(mapper -> {
                    mapper.map(DiagnosisDto::getId, DiagnosisViewModel::setId);
                    mapper.map(DiagnosisDto::getDiagnosisName, DiagnosisViewModel::setDiagnosisName);
                    mapper.map(DiagnosisDto::getDescription, DiagnosisViewModel::setDescription);
                    mapper.map(src -> src.getIssuedBy() != null ? src.getIssuedBy().getDoctorName() : "Unknown",
                            DiagnosisViewModel::setDoctorName);


                });
            }

            modelMapper.createTypeMap(DiagnosisDto.class, CreateDiagnosisViewModel.class).addMappings(mapper -> {
                mapper.map(DiagnosisDto::getDiagnosisName, CreateDiagnosisViewModel::setDiagnosisName);
                mapper.map(DiagnosisDto::getDescription, CreateDiagnosisViewModel::setDescription);


            });

            modelMapper.createTypeMap(SickLeaveDto.class, SickLeaveViewModel.class).addMappings(mapper -> {
                mapper.map(SickLeaveDto::getId, SickLeaveViewModel::setId);
                mapper.map(SickLeaveDto::getPatient, SickLeaveViewModel::setPatient);
                mapper.map(SickLeaveDto::getIssuedBy, SickLeaveViewModel::setIssuedBy);
                mapper.map(SickLeaveDto::getStartDate, SickLeaveViewModel::setStartDate);
                mapper.map(SickLeaveDto::getDayDuration, SickLeaveViewModel::setDayDuration);

            }).setPostConverter(context -> {
                SickLeaveDto source = context.getSource();
                SickLeaveViewModel destination = context.getDestination();


                if (source.getStartDate() != null && source.getDayDuration() > 0) {
                    destination.setEndDate(source.getStartDate().plusDays(source.getDayDuration()));
                }


                if (source.getIssuedBy() != null && source.getIssuedBy().getDoctorName() != null) {
                    destination.setDoctorName(source.getIssuedBy().getDoctorName());
                } else {
                    destination.setDoctorName("N/A");
                }


                if (source.getPatient() != null && source.getPatient().getName() != null) {
                    destination.setPatientName(source.getPatient().getName());
                } else {
                    destination.setPatientName("N/A");
                }

                return destination;


            });

            modelMapper.createTypeMap(TreatmentDto.class, TreatmentViewModel.class).addMappings(mapper -> {
                mapper.map(TreatmentDto::getId, TreatmentViewModel::setId);
                mapper.map(TreatmentDto::getInstructions, TreatmentViewModel::setInstructions);
                mapper.map(TreatmentDto::getIssuedBy, TreatmentViewModel::setIssuedByName);
                mapper.map(TreatmentDto::getMedications, TreatmentViewModel::setMedications);
                mapper.map(TreatmentDto::getPatient,  TreatmentViewModel::setPatientName);


            });

            {
                modelMapper.createTypeMap(Diagnosis.class, DiagnosisDto.class).addMappings(mapper -> {
                    mapper.map(Diagnosis::getId, DiagnosisDto::setId);
                    mapper.map(Diagnosis::getDiagnosisName, DiagnosisDto::setDiagnosisName);
                    mapper.map(Diagnosis::getDescription, DiagnosisDto::setDescription);
                    mapper.map(Diagnosis::getIssuedBy, DiagnosisDto::setIssuedBy);
                    mapper.map(src -> src.getIssuedBy().getId(), DiagnosisDto::setIssuedById);
                    mapper.map(Diagnosis::getPatient, DiagnosisDto::setPatient);   // ако го ползваш

                });


                modelMapper.createTypeMap(Visit.class, VisitDto.class).addMappings(mapper -> {
                    mapper.map(Visit::getId, VisitDto::setId);
                    mapper.map(Visit::getDoctor, VisitDto::setDoctor);
                    mapper.map(Visit::getPatient, VisitDto::setPatient);
                    mapper.map(Visit::getDiagnosis, VisitDto::setDiagnosis);
                    mapper.map(Visit::getTreatment, VisitDto::setTreatment);
                    mapper.map(Visit::getSickLeave, VisitDto::setSickLeave);
                    mapper.map(Visit::getLocalDate, VisitDto::setLocalDate);
                });

                modelMapper.createTypeMap(VisitDto.class, VisitViewModel.class).addMappings(mapper -> {
                    mapper.map(VisitDto::getId, VisitViewModel::setId);
                    mapper.map(VisitDto::getDoctor, VisitViewModel::setDoctor);
                    mapper.map(VisitDto::getPatient, VisitViewModel::setPatient);
                    mapper.map(VisitDto::getDiagnosis, VisitViewModel::setDiagnosis);
                    mapper.map(VisitDto::getTreatment, VisitViewModel::setTreatment);
                    mapper.map(VisitDto::getSickLeave, VisitViewModel::setSickLeave);
                    mapper.map(VisitDto::getLocalDate, VisitViewModel::setLocalDate);
                });

                modelMapper.createTypeMap(CreateVisitViewModel.class, UpdateVisitDto.class).addMappings(mapper -> {
                    mapper.map(CreateVisitViewModel::getId, UpdateVisitDto::setId);
                    mapper.map(CreateVisitViewModel::getDoctorId, UpdateVisitDto::setDoctorId);
                    mapper.map(CreateVisitViewModel::getPatientId, UpdateVisitDto::setPatientId);
                    mapper.map(CreateVisitViewModel::getLocalDate, UpdateVisitDto::setLocalDate);
                    mapper.map(CreateVisitViewModel::getDiagnosisId, UpdateVisitDto::setDiagnosisId);
                    mapper.map(CreateVisitViewModel::getTreatmentId, UpdateVisitDto::setTreatmentId);
                    mapper.map(CreateVisitViewModel::getSickLeaveId, UpdateVisitDto::setSickLeaveId);
                });

                modelMapper.createTypeMap(CreateVisitViewModel.class, CreateVisitDto.class).addMappings(mapper -> {
                    mapper.map(CreateVisitViewModel::getDoctorId, CreateVisitDto::setDoctorId);
                    mapper.map(CreateVisitViewModel::getPatientId, CreateVisitDto::setPatientId);
                    mapper.map(CreateVisitViewModel::getLocalDate, CreateVisitDto::setLocalDate);
                    mapper.map(CreateVisitViewModel::getDiagnosisId, CreateVisitDto::setDiagnosisId);
                    mapper.map(CreateVisitViewModel::getTreatmentId, CreateVisitDto::setTreatmentId);
                    mapper.map(CreateVisitViewModel::getSickLeaveId, CreateVisitDto::setSickLeaveId);
                });

                if (modelMapper.getTypeMap(Diagnosis.class, DiagnosisViewModel.class) == null) {

                    modelMapper.createTypeMap(Diagnosis.class, DiagnosisViewModel.class)
                            .addMappings(mapper -> {
                                mapper.map(Diagnosis::getId, DiagnosisViewModel::setId);
                                mapper.map(Diagnosis::getDiagnosisName, DiagnosisViewModel::setDiagnosisName);
                                mapper.map(Diagnosis::getDescription, DiagnosisViewModel::setDescription);
                                mapper.map(src -> src.getIssuedBy() != null ? src.getIssuedBy().getDoctorName() : "", DiagnosisViewModel::setDoctorName);


                            });

                    modelMapper.createTypeMap(Medicine.class, MedicineDto.class).addMappings(mapper -> {
                        mapper.map(Medicine::getId, MedicineDto::setId);
                        mapper.map(Medicine::getMedicineName, MedicineDto::setMedicineName);
                        mapper.map(Medicine::getDosage, MedicineDto::setDosage);
                    });

                    modelMapper.createTypeMap(MedicineDto.class, MedicineViewModel.class).addMappings(mapper -> {
                        mapper.map(MedicineDto::getId, MedicineViewModel::setId);
                        mapper.map(MedicineDto::getMedicineName, MedicineViewModel::setMedicineName);
                        mapper.map(MedicineDto::getDosage, MedicineViewModel::setDosage);
                    });

                    modelMapper.createTypeMap(MedicineDto.class, CreateMedicineViewModel.class).addMappings(mapper -> {
                        mapper.map(MedicineDto::getMedicineName, CreateMedicineViewModel::setMedicineName);
                        mapper.map(MedicineDto::getDosage, CreateMedicineViewModel::setDosage);
                    });

                    modelMapper.createTypeMap(CreateMedicineViewModel.class, CreateMedicineDto.class)
                            .addMappings(mapper -> {
                                mapper.map(CreateMedicineViewModel::getMedicineName, CreateMedicineDto::setMedicineName);
                                mapper.map(CreateMedicineViewModel::getDosage, CreateMedicineDto::setDosage);
                            });


                    modelMapper.createTypeMap(CreateMedicineViewModel.class, MedicineDto.class).addMappings(mapper -> {
                        mapper.map(CreateMedicineViewModel::getMedicineName, MedicineDto::setMedicineName);
                        mapper.map(CreateMedicineViewModel::getDosage, MedicineDto::setDosage);
                    });

                    modelMapper.createTypeMap(MedicineViewModel.class, MedicineDto.class).addMappings(mapper -> {
                        mapper.map(MedicineViewModel::getId, MedicineDto::setId);
                        mapper.map(MedicineViewModel::getMedicineName, MedicineDto::setMedicineName);
                        mapper.map(MedicineViewModel::getDosage, MedicineDto::setDosage);
                    });

                    modelMapper.createTypeMap(CreateTreatmentDto.class, Treatment.class)
                            .addMappings(mapper -> {
                                mapper.map(CreateTreatmentDto::getInstructions, Treatment::setInstructions);
                                mapper.map(CreateTreatmentDto::getMedicationIds, Treatment::setMedications);
                                mapper.skip(Treatment::setId);
                                mapper.skip(Treatment::setIssuedBy);
                                mapper.skip(Treatment::setPatient);

                            });
                    modelMapper.createTypeMap(TreatmentDto.class, CreateTreatmentViewModel.class).addMappings(mapper -> {
                        mapper.map(TreatmentDto::getInstructions, CreateTreatmentViewModel::setInstructions);
                        mapper.map(TreatmentDto::getMedications, CreateTreatmentViewModel::setMedicationIds);


                    });


                }
            }
        }
    }
}