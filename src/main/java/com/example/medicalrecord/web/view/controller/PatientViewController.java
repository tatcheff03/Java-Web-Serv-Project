package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.dto.CreatePatientDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.dto.PatientDto;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.CreatePatientViewModel;
import com.example.medicalrecord.web.view.controller.model.PatientViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientViewController {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final MapperUtil mapperUtil;

    @GetMapping
    public String showAllPatients(Model model) {
        List<PatientDto> patientDtos = patientService.getAllPatients();
        List<PatientViewModel> patients = patientDtos.stream()
                .map(dto -> mapperUtil.map(dto, PatientViewModel.class))
                .collect(Collectors.toList());
        model.addAttribute("patients", patients);
        return "patients/patient-list";
    }

    @GetMapping("/create")
    public String showCreatePatientForm(Model model) {
        model.addAttribute("patient", new CreatePatientViewModel());
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "patients/create-patient";
    }

    @PostMapping("/create")
    public String createPatient(@ModelAttribute("patient") CreatePatientViewModel viewModel) {
        CreatePatientDto dto = mapperUtil.map(viewModel, CreatePatientDto.class);
        patientService.createPatient(dto);
        return "redirect:/patients";

    }

    @GetMapping("delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    public String editPatientForm(@PathVariable Long id, Model model) {
        PatientDto patientDto = patientService.getPatientById(id);
        CreatePatientViewModel viewModel = mapperUtil.map(patientDto, CreatePatientViewModel.class);
        model.addAttribute("patient", viewModel);

        List<DoctorDto> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        return "patients/edit-patient";
    }

    @PostMapping("/edit")
    public String submitPatient(@ModelAttribute("patient") CreatePatientViewModel viewModel) {
        CreatePatientDto dto = mapperUtil.map(viewModel, CreatePatientDto.class);
        patientService.updatePatient(viewModel.getPersonalDoctorId(), dto); // or use viewModel.getId() if you include ID
        return "redirect:/patients";
    }
}
