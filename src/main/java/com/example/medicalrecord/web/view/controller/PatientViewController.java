package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;


import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;


import java.nio.charset.StandardCharsets;
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
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showAllPatients(Model model, Authentication authentication) {
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        List<PatientDto> patientDtos = patientService.getAllActivePatients();

        if (isDoctor && !isAdmin) {
            DoctorDto loggedInDoctor = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            model.addAttribute("loggedInDoctorId", loggedInDoctor.getId());

            patientDtos = patientDtos.stream()
                    .filter(p -> p.getPersonalDoctor().getId().equals(loggedInDoctor.getId()))
                    .toList();
        }

        List<PatientViewModel> patients = patientDtos.stream()
                .map(dto -> mapperUtil.map(dto, PatientViewModel.class))
                .collect(Collectors.toList());

        model.addAttribute("patients", patients);
        return "patients/patient-list";
    }



    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreatePatientForm(
            @RequestParam(value = "from", required = false) String from,
            Model model,
            Authentication auth
    ) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String encoded = UriUtils.encode("/patients", StandardCharsets.UTF_8);
            return "redirect:/unauthorized?from=" + encoded;
        }

        model.addAttribute("patient", new CreatePatientViewModel());
        model.addAttribute("from", from);
        model.addAttribute("doctors", doctorService.getAllDoctors());

        return "patients/create-patient";
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createPatient(@ModelAttribute("patient") @Valid CreatePatientViewModel viewModel,
                                BindingResult bindingResult,
                                Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("doctors", doctorService.getAllDoctors());
            return "patients/create-patient";
        }

        CreatePatientDto dto = mapperUtil.map(viewModel, CreatePatientDto.class);
        patientService.createPatient(dto);
        return "redirect:/patients";

    }

    @GetMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            patientService.deletePatient(id);
            redirectAttributes.addFlashAttribute("successMessage", "Patient archived successfully.");
        } catch (SoftDeleteException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error: " + e.getMessage());
    }
        return "redirect:/patients";
    }


    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String editPatientForm(@PathVariable Long id, Model model, Authentication auth) {
        PatientDto patientDto = patientService.getPatientById(id);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(("ROLE_DOCTOR")))) {
            String username = auth.getName();
            DoctorDto currentDoctor = doctorService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            if (!patientDto.getPersonalDoctor().getId().equals(currentDoctor.getId())) {
                throw new AccessDeniedException("You do not have permission to edit this patient.");
            }
        }



        CreatePatientViewModel viewModel = mapperUtil.map(patientDto, CreatePatientViewModel.class);
        viewModel.setUsername(patientDto.getUsername() );
        model.addAttribute("patient", viewModel);

        List<DoctorDto> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        DoctorDto selectedDoctor = doctorService.getDoctorById(viewModel.getPersonalDoctorId());
        model.addAttribute("doctorName", selectedDoctor.getDoctorName());

        return "patients/edit-patient";
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('PATIENT')")
    public String showPatientHistory(Model model, Authentication auth) {


        String username = auth.getName();
        Patient patient = patientService.findByUsername(username).orElse(null);

        if (patient == null) {
            return "redirect:/unauthorized?from=/patients";
        }

        List<VisitViewModel> visits = visitService.getVisitsByPatientId(patient.getId());
        model.addAttribute("visits", visits);
        return "patients/patient-history";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String submitPatient(@ModelAttribute("patient") @Valid CreatePatientViewModel viewModel,
                                BindingResult  bindingResult,
                                Model model,
                                Authentication auth) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("doctorName", doctorService.getDoctorById(viewModel.getPersonalDoctorId()).getDoctorName());
            return "patients/edit-patient";
        }
        String username = auth.getName();

        boolean isDoctor = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isDoctor && !isAdmin) {
            DoctorDto currentDoctor = doctorService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            PatientDto existing = patientService.getPatientById(viewModel.getId());

            if (!existing.getPersonalDoctor().getId().equals(currentDoctor.getId())) {
                throw new AccessDeniedException("You do not have permission to update this patient.");
            }
        }

        CreatePatientDto dto = mapperUtil.map(viewModel, CreatePatientDto.class);
        patientService.updatePatient(viewModel.getId(), dto);
        return "redirect:/patients";
    }


    @GetMapping("/archived")
    @PreAuthorize("hasRole('ADMIN')")
    public String showArchivedPatients(Model model) {
        List<PatientDto> archived = patientService.getAllDeletedPatients();
        model.addAttribute("patients", archived);
        return "patients/archived-patient-list";
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String restorePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        patientService.restorePatient(id);
        redirectAttributes.addFlashAttribute("successMessage", "Patient restored successfully.");
        return "redirect:/patients/archived";
    }



}
