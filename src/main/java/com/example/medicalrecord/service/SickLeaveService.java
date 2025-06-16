package com.example.medicalrecord.service;

import com.example.medicalrecord.data.entity.Doctor;
import com.example.medicalrecord.dto.CreateSickLeaveDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.dto.SickLeaveDto;

import java.time.Month;
import java.util.List;
import java.util.Map;

public interface SickLeaveService {
    SickLeaveDto createSickLeave(CreateSickLeaveDto dto);
    List<SickLeaveDto> getAllSickLeaves();
    SickLeaveDto getSickLeaveById(Long id);
    SickLeaveDto updateSickLeave(Long id, CreateSickLeaveDto dto);
    void deleteSickLeave(Long id);
    List<SickLeaveDto> getAllDeletedSickLeaves();
    void restoreSickLeave(Long id);
    List<SickLeaveDto> getAllSickLeavesByPatientId(Long patientId);
    List<SickLeaveDto> getAllSickLeavesByDoctorId(Long doctorId);
    Map.Entry<Month, Long> getMonthWithMostSickLeaves();
    Map<Doctor, Long> getDoctorsWithMostSickLeaves();


}
