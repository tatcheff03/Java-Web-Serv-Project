package com.example.medicalrecord.web.view.controller;


import com.example.medicalrecord.dto.CreateTreatmentDto;
import com.example.medicalrecord.dto.TreatmentDto;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.MedicineService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.service.TreatmentService;
import com.example.medicalrecord.web.view.controller.model.CreateTreatmentViewModel;
import com.example.medicalrecord.web.view.controller.model.TreatmentViewModel;
import com.example.medicalrecord.dto.*;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

import com.example.medicalrecord.util.OwnershipUtil;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/treatment")
@RequiredArgsConstructor
public class TreatmentViewController {

    private final TreatmentService treatmentService;
    private final MedicineService medicineService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String viewAllTreatments(Model model,Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        List<TreatmentDto> treatments = treatmentService.getAllActiveTreatments();
        List<TreatmentViewModel> viewModels = treatments.stream().map(dto -> {
            TreatmentViewModel vm = new TreatmentViewModel();
            vm.setId(dto.getId());

            vm.setInstructions(dto.getInstructions());
            vm.setMedications(dto.getMedications().stream()
                    .map(MedicineDto::getMedicineName)
                    .toList());
            if (isAdmin && dto.getPatientId() != null) {
                try {
                    PatientDto patient = patientService.getPatientById(dto.getPatientId());
                    vm.setPatientName(patient.getName());
                } catch (RuntimeException e) {
                    vm.setPatientName("Unknown");
                }
            }

            return vm;

        }).toList();

        model.addAttribute("treatments", viewModels);
        return "treatment/treatments-list";
    }
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public String showCreateForm(Model model, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long doctorId = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

        List<PatientDto> patients;

        if (isDoctor && !isAdmin) {
            patients = patientService.getPatientsByDoctorId(doctorId);
        } else {
            patients = patientService.getAllActivePatients();
        }

        model.addAttribute("treatment", new CreateTreatmentViewModel());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("patients", patients);

        return "treatment/treatments-create";
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public String createTreatment(@ModelAttribute("treatment") CreateTreatmentViewModel viewModel,
                                  Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        Long doctorId = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

        CreateTreatmentDto dto = new CreateTreatmentDto();
        dto.setInstructions(viewModel.getInstructions());
        dto.setMedicationIds(viewModel.getMedicationIds());
        dto.setIssuedById(doctorId);
        dto.setPatientId(viewModel.getPatientId());

        treatmentService.createTreatment(dto);
        return "redirect:/treatment";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        TreatmentDto treatment = treatmentService.getTreatmentById(id);
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        Long doctorId = null;
        List<PatientDto> patients;

        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        if (isDoctor) {
            doctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

            OwnershipUtil.verifyOwnership(treatment.getIssuedBy().getId(), doctorId,
                    "You cannot edit another doctor's treatment.");

            // Вземи пациенти на лекаря
            patients = new ArrayList<>(patientService.getPatientsByDoctorId(doctorId));

            // Увери се, че добавяме и текущия пациент ако липсва в списъка
            Long treatmentPatientId = treatment.getPatientId();
            boolean alreadyIncluded = patients.stream().anyMatch(p -> p.getId().equals(treatmentPatientId));
            if (!alreadyIncluded) {
                PatientDto patient = patientService.getPatientById(treatmentPatientId);
                if (patient != null) {
                    patients.add(patient);
                }
            }

        } else {

            patients = patientService.getAllActivePatients();
        }

        CreateTreatmentViewModel viewModel = new CreateTreatmentViewModel();
        viewModel.setInstructions(treatment.getInstructions());
        viewModel.setMedicationIds(treatment.getMedications().stream()
                .map(MedicineDto::getId).collect(Collectors.toSet()));
        viewModel.setPatientId(treatment.getPatientId());


        String patientName = patients.stream()
                .filter(p -> p.getId().equals(treatment.getPatientId()))
                .findFirst()
                .map(PatientDto::getName)
                .orElseGet(() -> treatment.getPatient() != null ? treatment.getPatient().getName() : "Unknown");

        viewModel.setPatientName(patientName);

        model.addAttribute("treatment", viewModel);
        model.addAttribute("treatmentId", id);
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("patients", patients);

        return "treatment/treatments-edit";
    }


    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String editTreatment(@PathVariable Long id,
                                @ModelAttribute("treatment") CreateTreatmentViewModel viewModel,
                                Authentication authentication) {
        TreatmentDto existing = treatmentService.getTreatmentById(id);
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            Long doctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

            OwnershipUtil.verifyOwnership(existing.getIssuedBy().getId(), doctorId,
                    "You cannot edit another doctor's treatment.");
        }

        CreateTreatmentDto dto = new CreateTreatmentDto();
        dto.setInstructions(viewModel.getInstructions());
        dto.setMedicationIds(viewModel.getMedicationIds());
        dto.setPatientId(viewModel.getPatientId());

        treatmentService.updateTreatment(id, dto);
        return "redirect:/treatment";
    }


    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String deleteTreatment(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        TreatmentDto treatment = treatmentService.getTreatmentById(id);
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        if (treatment.getIssuedBy() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Treatment has no assigned doctor.");
            return "redirect:/treatment";
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            Long doctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();
            try {
                OwnershipUtil.verifyOwnership(treatment.getIssuedBy().getId(), doctorId, "You cannot delete another doctor's treatment.");
            } catch (SecurityException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/unauthorized";
            }
        }

        try {
            treatmentService.deleteTreatment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Treatment archived successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/treatment";
    }

    @GetMapping("/archived")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showArchivedTreatments(Model model, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        Long doctorId = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

        List<TreatmentDto> archived;
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            archived = treatmentService.getAllDeletedTreatments();
        } else {
            archived = treatmentService.getAllDeletedTreatmentsByDoctorId(doctorId);
        }

        List<TreatmentViewModel> viewModels = archived.stream().map(dto -> {
            TreatmentViewModel vm = new TreatmentViewModel();
            vm.setId(dto.getId());
            vm.setIssuedByName(dto.getIssuedBy() != null ? dto.getIssuedBy().getDoctorName() : "Unknown");
            vm.setInstructions(dto.getInstructions());
            vm.setMedications(dto.getMedications().stream()
                    .map(MedicineDto::getMedicineName)
                    .toList());
            return vm;
        }).toList();

        model.addAttribute("treatments", viewModels);
        return "treatment/archived-treatments";
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String restoreTreatment(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        TreatmentDto treatment = treatmentService.getTreatmentById(id);
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            Long doctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();
            OwnershipUtil.verifyOwnership(treatment.getIssuedBy().getId(), doctorId, "You cannot restore another doctor's treatment.");
        }

        treatmentService.restoreTreatment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Treatment restored successfully.");
        return "redirect:/treatment/archived";
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public String viewMyTreatments(Model model, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        Long doctorId = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

        List<TreatmentDto> treatments = treatmentService.getTreatmentsByDoctorId(doctorId);

        List<TreatmentViewModel> viewModels = treatments.stream().map(dto -> {
            TreatmentViewModel vm = new TreatmentViewModel();
            vm.setId(dto.getId());
            vm.setInstructions(dto.getInstructions());
            vm.setMedications(dto.getMedications().stream()
                    .map(MedicineDto::getMedicineName)
                    .toList());
            vm.setIssuedByName(dto.getIssuedBy().getDoctorName());
            return vm;
        }).toList();

        model.addAttribute("treatments", viewModels);
        return "doctor/doc-treatments";
    }
}
