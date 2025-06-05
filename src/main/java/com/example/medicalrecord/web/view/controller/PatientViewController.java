package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.data.entity.Patient;
import com.example.medicalrecord.dto.CreatePatientDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.dto.PatientDto;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.service.VisitService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.CreatePatientViewModel;
import com.example.medicalrecord.web.view.controller.model.PatientViewModel;
import com.example.medicalrecord.web.view.controller.model.VisitViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientViewController {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final VisitService visitService;
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
    public String editPatientForm(@PathVariable Long id, Model model, Authentication auth) {
        PatientDto patientDto = patientService.getPatientById(id);
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(("ROLE_DOCTOR")))) {
            String username = auth.getName();
            DoctorDto currentDoctor = doctorService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            if (!patientDto.getPersonalDoctor().getId().equals(currentDoctor.getId())) {
                throw new AccessDeniedException("You do not have permission to edit this patient.");
            }
        }

        CreatePatientViewModel viewModel = mapperUtil.map(patientDto, CreatePatientViewModel.class);
        model.addAttribute("patient", viewModel);

        List<DoctorDto> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        return "patients/edit-patient";
    }

    @GetMapping("/history")
    public String showPatientHistory(Model model, Authentication auth) {
        System.out.println("✅ Authentication class: " + auth.getClass().getName());
        System.out.println("✅ Authorities: " + auth.getAuthorities());
        System.out.println("✅ Principal: " + auth.getPrincipal());

        String username = auth.getName();
        Patient patient = patientService.findByUsername(username);


        List<VisitViewModel> visits = visitService.getVisitsByPatientId(patient.getId());
        model.addAttribute("visits", visits);
        return "patients/patient-history";
    }

    @PostMapping("/edit")
    public String submitPatient(@ModelAttribute("patient") CreatePatientViewModel viewModel) {
        CreatePatientDto dto = mapperUtil.map(viewModel, CreatePatientDto.class);
        patientService.updatePatient(viewModel.getId(), dto);
        return "redirect:/patients";
    }






}
