package com.example.medicalrecord.web.view.controller;


import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.repo.DoctorRepository;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.util.OwnershipUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import com.example.medicalrecord.service.DiagnosisService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.CreateDiagnosisViewModel;
import com.example.medicalrecord.web.view.controller.model.DiagnosisViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.medicalrecord.dto.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class DiagnosisViewController {

    private final DiagnosisService diagnosisService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final MapperUtil mapperUtil;
    private final DoctorRepository doctorRepository;


    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String listDiagnoses(Model model) {
        List<DiagnosisDto> diagnosisDtos = diagnosisService.getAllActiveDiagnoses();

        List<DiagnosisViewModel> diagnosis = diagnosisDtos.stream()
                .map(dto -> {
                    DiagnosisViewModel vm = new DiagnosisViewModel();
                    vm.setId(dto.getId());
                    vm.setDiagnosisName(dto.getDiagnosisName());
                    vm.setDescription(dto.getDescription());
                    if (dto.getIssuedBy() != null) {
                        vm.setDoctorName(dto.getIssuedBy().getDoctorName());
                    } else {
                        vm.setDoctorName("Unknown");
                    }
                    return vm;
                })
                .toList();

        model.addAttribute("diagnoses", diagnosis);
        return "diagnosis/diagnosislist";
    }


    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showCreateForm(Model model, Authentication authentication) {


        List<PatientDto> patients;
        boolean isOnlyDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")) &&
                authentication.getAuthorities().stream()
                        .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));



        if (isOnlyDoctor) {
            Long doctorId = getDoctorIdIfDoctor(authentication);
            patients = patientService.getPatientsByDoctorId(doctorId);
        } else {
            patients = patientService.getAllActivePatients();

        }


        CreateDiagnosisViewModel vm = new CreateDiagnosisViewModel();
        model.addAttribute("diagnosis", vm);
     //   model.addAttribute("diagnosis", new CreateDiagnosisViewModel());
        model.addAttribute("patients", patients);
        return "diagnosis/diagnosiscreate";
    }



    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public String createDiagnosis(@ModelAttribute("diagnosis") CreateDiagnosisViewModel viewModel,
                                  Authentication authentication) {
        Long doctorId = getDoctorIdIfDoctor(authentication);


        if (doctorId == null) {
            String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
            doctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin not mapped to doctor")).getId();
        }

        CreateDiagnosisDto dto = new CreateDiagnosisDto();
        dto.setDiagnosisName(viewModel.getDiagnosisName());
        dto.setDescription(viewModel.getDescription());
        dto.setIssuedById(doctorId);
        dto.setPatientId(viewModel.getPatientId());

        diagnosisService.createDiagnosis(dto);
        return "redirect:/diagnoses";
    }


    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public String viewMyDiagnoses(Model model, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        Long doctorId = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"))
                .getId();

        List<DiagnosisDto> diagnoses = diagnosisService.getDiagnosesByDoctorId(doctorId);

        List<DiagnosisViewModel> viewModels = diagnoses.stream().map(dto -> {
            DiagnosisViewModel vm = new DiagnosisViewModel();
            vm.setId(dto.getId());
            vm.setDiagnosisName(dto.getDiagnosisName());
            vm.setDescription(dto.getDescription());
            vm.setDoctorName(dto.getIssuedBy().getDoctorName());

            return vm;
        }).toList();


        model.addAttribute("diagnoses", viewModels);
        return "doctor/doc-diagnoses";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        DiagnosisDto dto = diagnosisService.getDiagnosisById(id);
        Long doctorId = getDoctorIdIfDoctor(authentication);

        if (!isAdmin(authentication) && doctorId != null) {
            OwnershipUtil.verifyOwnership(dto.getIssuedBy().getId(), doctorId,
                    "You cannot edit another doctor's diagnosis.");
        }

        CreateDiagnosisViewModel viewModel = mapperUtil.map(dto, CreateDiagnosisViewModel.class);
        viewModel.setPatientName(dto.getPatient().getName());
        model.addAttribute("diagnosis", viewModel);
        model.addAttribute("id", id);
        return "diagnosis/diagnosisedit";
    }



    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String updateDiagnosis(@PathVariable Long id,
                                  @ModelAttribute("diagnosis") CreateDiagnosisViewModel updateModel,
                                  Authentication authentication) {
        DiagnosisDto existing = diagnosisService.getDiagnosisById(id);
        Long doctorId = getDoctorIdIfDoctor(authentication);

        if (!isAdmin(authentication) && doctorId != null) {
            OwnershipUtil.verifyOwnership(existing.getIssuedBy().getId(), doctorId,
                    "You cannot edit another doctor's diagnosis.");
        }

        CreateDiagnosisDto dto = new CreateDiagnosisDto();
        dto.setDiagnosisName(updateModel.getDiagnosisName());
        dto.setDescription(updateModel.getDescription());
        dto.setIssuedById(doctorId);
        diagnosisService.updateDiagnosis(id, dto);
        return "redirect:/diagnoses";
    }


    @GetMapping("/archived")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showArchivedDiagnoses(Model model, Authentication authentication) {

        List<DiagnosisDto> archivedDtos;

        if (isAdmin(authentication)) {
            archivedDtos = diagnosisService.getAllDeletedDiagnoses();
        } else {
            Long doctorId = getDoctorIdIfDoctor(authentication);
            archivedDtos = diagnosisService.getAllDeletedDiagnosesByDoctorId(doctorId);
        }



        List<DiagnosisViewModel> archived = archivedDtos.stream()
                .map(dto -> {
                    DiagnosisViewModel vm = new DiagnosisViewModel();
                    vm.setId(dto.getId());
                    vm.setDiagnosisName(dto.getDiagnosisName());
                    vm.setDescription(dto.getDescription());
                    if (dto.getIssuedBy() != null) {
                        vm.setDoctorName(dto.getIssuedBy().getDoctorName());
                    } else {
                        vm.setDoctorName("Unknown");
                    }
                    return vm;
                })
                .toList();


        model.addAttribute("diagnoses", archived);
        return "diagnosis/archived-diagnosis-list";
    }


    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String restoreDiagnosis(@PathVariable Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        DiagnosisDto dto = diagnosisService.getDiagnosisById(id);
        Long doctorId = getDoctorIdIfDoctor(authentication);

        if (!isAdmin(authentication) && doctorId != null) {
            OwnershipUtil.verifyOwnership(dto.getIssuedBy().getId(), doctorId,
                    "You cannot edit another doctor's diagnosis.");
        }

        diagnosisService.restoreDiagnosis(id);
        redirectAttributes.addFlashAttribute("successMessage", "Diagnosis restored successfully.");
        return "redirect:/diagnoses";
    }


    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String deleteDiagnosis(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        DiagnosisDto existing = diagnosisService.getDiagnosisById(id);
        Long doctorId = getDoctorIdIfDoctor(authentication);

        if (!isAdmin(authentication) && doctorId != null) {
            OwnershipUtil.verifyOwnership(existing.getIssuedBy().getId(), doctorId,
                    "You cannot edit another doctor's diagnosis.");
        }

        try {
            diagnosisService.deleteDiagnosis(id);
            redirectAttributes.addFlashAttribute("successMessage", "Diagnosis archived successfully ✅");
        } catch (SoftDeleteException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error: " + e.getMessage());
        }

        return "redirect:/diagnoses";
    }


    @GetMapping("/report/by-diagnosis")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showPatientsByDiagnosisForm(Model model) {
        List<String> diagnosisNames = diagnosisService.getAllDiagnosis().stream()
                .filter(dto -> dto.getPatient() != null) // или getPatient() != null
                .map(DiagnosisDto::getDiagnosisName)
                .distinct()
                .toList();

        model.addAttribute("diagnosisNames", diagnosisNames);
        return "diagnosis/patients-by-diagnosis-form";
    }


    @PostMapping("/report/by-diagnosis")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showPatientsForDiagnosis(@RequestParam String diagnosisName, Model model) {
        List<PatientDto> patients = diagnosisService.getPatientsByDiagnosisName(diagnosisName);
        model.addAttribute("patients", patients);
        model.addAttribute("diagnosisName", diagnosisName);
        return "diagnosis/patients-by-diagnosis-result";
    }

    @GetMapping("/report/common-diagnoses")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showMostCommonDiagnoses(Model model) {
        List<DiagnosisCountDto> stats = diagnosisService.getMostCommonDiagnoses();
        model.addAttribute("diagnosisStats", stats);
        return "diagnosis/common-diagnosis-report";
    }

    private Long getDoctorIdIfDoctor(Authentication auth) {
        String username = ((OidcUser) auth.getPrincipal()).getPreferredUsername();
        boolean isDoctor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        if (isDoctor) {
            return doctorRepository.findByUsernameAndDeletedFalse(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();
        }

        return null;
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }





}
