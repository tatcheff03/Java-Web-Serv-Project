package com.example.medicalrecord.web.view.controller;


import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.web.view.controller.model.CreateVisitViewModel;
import com.example.medicalrecord.web.view.controller.model.VisitViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import com.example.medicalrecord.service.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitViewController {
    private final VisitService visitService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final TreatmentService treatmentService;
    private final SickLeaveService sickLeaveService;
    private final MapperUtil mapperUtil;


    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String listVisits(Model model, Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isDoctorOnly = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")) && !isAdmin;

        List<VisitDto> allVisits = visitService.getAllVisits();

        List<VisitDto> filteredVisits;

        if (isDoctorOnly) {
            DoctorDto doctor = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            filteredVisits = allVisits.stream()
                    .filter(v -> v.getDoctor().getId().equals(doctor.getId()))
                    .toList();
        } else {

            filteredVisits = allVisits;
        }

        List<VisitViewModel> visits = filteredVisits.stream()
                .map(dto -> mapperUtil.map(dto, VisitViewModel.class))
                .toList();

        model.addAttribute("visits", visits);
        return "visits/visits-list";
    }


    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public String showCreateForm(@RequestParam(required = false) Long patientId,
                                 Model model,
                                 Authentication authentication) {

        String username = authentication.getName();

        DoctorDto loggedInDoctor = doctorService.findByUsername(username).orElseThrow(() -> new RuntimeException("Doctor not found"));
        model.addAttribute("loggedInDoctorId", loggedInDoctor.getId());



        if (patientId != null) {
            PatientDto patient = patientService.getPatientById(patientId);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin && !patient.getPersonalDoctor().getId().equals(loggedInDoctor.getId())) {
                throw new AccessDeniedException("You cannot create a visit for a patient that is not yours.");
            }

            model.addAttribute("sickLeaves", sickLeaveService.getAllSickLeavesByPatientId(patientId));
        } else {
            model.addAttribute("sickLeaves", new ArrayList<>());
        }

        CreateVisitViewModel viewModel = new CreateVisitViewModel();
        viewModel.setPatientId(patientId);
        viewModel.setDoctorId(loggedInDoctor.getId());
        model.addAttribute("visit", viewModel);

        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllActivePatients());
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnosis());
        model.addAttribute("treatments", treatmentService.getAllTreatments());

        return "visits/visits-create";
    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public String createVisit(@ModelAttribute("visit") CreateVisitViewModel visitViewModel, Authentication authentication) {
        String username = authentication.getName();
        DoctorDto doctor = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !doctor.getId().equals(visitViewModel.getDoctorId())) {
            throw new AccessDeniedException("You are not allowed to create a visit with another doctor.");
        }

        CreateVisitDto dto = mapperUtil.map(visitViewModel, CreateVisitDto.class);
        visitService.createVisit(dto);
        return "redirect:/visits";
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping("/edit/{id}")

    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        VisitDto dto = visitService.getVisitById(id);

        String username = authentication.getName();
        if (!dto.getDoctor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to edit this visit.");
        }

        CreateVisitViewModel viewModel = new CreateVisitViewModel();
        viewModel.setId(dto.getId());
        viewModel.setDoctorId(dto.getDoctor().getId());
        viewModel.setPatientId(dto.getPatient().getId());
        viewModel.setLocalDate(dto.getLocalDate());
        viewModel.setDiagnosisId(dto.getDiagnosis().getId());

        if (dto.getTreatment() != null) {
            viewModel.setTreatmentId(dto.getTreatment().getId());
        }

        if (dto.getSickLeave() != null) {
            viewModel.setSickLeaveId(dto.getSickLeave().getId());
        }

        DoctorDto doctor = doctorService.getDoctorById(viewModel.getDoctorId());
        model.addAttribute("doctorName", doctor.getDoctorName());

        model.addAttribute("visit", viewModel);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllActivePatients());
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnosis());
        model.addAttribute("treatments", treatmentService.getAllTreatments());
        model.addAttribute("sickLeaves", sickLeaveService.getAllSickLeavesByPatientId(dto.getPatient().getId()));

        return "visits/visits-edit";
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PostMapping("/edit")
    public String editVisit(@ModelAttribute("visit") CreateVisitViewModel viewModel, Authentication authentication) {
        VisitDto existing = visitService.getVisitById(viewModel.getId());

        String username = authentication.getName();
        if (!existing.getDoctor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to update this visit.");
        }

        UpdateVisitDto dto = mapperUtil.map(viewModel, UpdateVisitDto.class);
        visitService.updateVisit(dto.getId(), dto);
        return "redirect:/visits";
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteVisit(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication authentication) {
        VisitDto visit = visitService.getVisitById(id);

        String username = authentication.getName();
        if (!visit.getDoctor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to delete this visit.");
        }

        try {
            visitService.deleteVisit(id);
            redirectAttributes.addFlashAttribute("successMessage", "Visit deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/visits";
    }


    @GetMapping("/archived")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')") // ако го няма
    public String showArchivedVisits(Model model, Authentication authentication) {
        String username = authentication.getName();
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        List<VisitDto> allDeleted = visitService.getAllDeletedVisits();

        List<VisitDto> filteredVisits;
        if (isDoctor) {
            DoctorDto doctor = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            filteredVisits = allDeleted.stream()
                    .filter(v -> v.getDoctor().getId().equals(doctor.getId()))
                    .toList();
        } else {
            filteredVisits = allDeleted;
        }

        List<VisitViewModel> viewModels = filteredVisits.stream()
                .map(dto -> mapperUtil.map(dto, VisitViewModel.class))
                .toList();

        model.addAttribute("visits", viewModels);
        return "visits/archived-visits";
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping("/restore/{id}")
    public String restoreVisit(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication authentication) {
        String username = authentication.getName();
        VisitDto visit = visitService.getVisitById(id);

        if (!visit.getDoctor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to restore this visit.");
        }

        visitService.restoreVisit(id);
        redirectAttributes.addFlashAttribute("successMessage", "Visit restored successfully.");
        return "redirect:/visits";
    }

    @GetMapping("/report/visit-count")
    @PreAuthorize("hasRole('ADMIN')")
    public String showVisitCountPerDoctor(Model model) {
        Map<String, Long> visitsPerDoctor = visitService.getVisitCountPerDoctor();
        model.addAttribute("visitStats", visitsPerDoctor);
        return "visits/visit-count-report";
    }

    @GetMapping("/report/by-patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String showPatientVisitForm(Model model) {
        List<PatientDto> patients = patientService.getAllActivePatients();
        model.addAttribute("patients", patients);
        return "visits/patient-visit-form";
    }

    @PostMapping("/report/by-patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String showVisitsForPatient(@RequestParam Long patientId, Model model) {
        List<VisitViewModel> visits = visitService.getVisitsByPatientId(patientId);


        model.addAttribute("visits", visits);
        return "visits/patient-visit-result";
    }

    @GetMapping("/report/by-period")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDateFilterForm() {
        return "visits/visit-period-form";
    }

    @PostMapping("/report/by-period")
    @PreAuthorize("hasRole('ADMIN')")
    public String showVisitsInPeriod(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                     @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                     Model model) {
        List<VisitViewModel> visits = visitService.getVisitsBetweenDates(start, end);
        model.addAttribute("visits", visits);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        return "visits/visit-period-result";
    }


    @GetMapping("/report/by-doctor-and-period")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDoctorPeriodForm(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "visits/visit-doctor-period-form";
    }

    @PostMapping("/report/by-doctor-and-period")
    @PreAuthorize("hasRole('ADMIN')")
    public String showVisitsByDoctorAndPeriod(@RequestParam Long doctorId,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                              Model model) {

        List<VisitViewModel> visits = visitService.getVisitsByDoctorAndPeriod(doctorId, startDate, endDate);
        model.addAttribute("visits", visits);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("doctor", doctorService.getDoctorById(doctorId));
        return "visits/visit-doctor-period-result";
    }


}
