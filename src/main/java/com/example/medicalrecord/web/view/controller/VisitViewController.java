package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.dto.*;
import com.example.medicalrecord.web.view.controller.model.CreateVisitViewModel;
import com.example.medicalrecord.web.view.controller.model.VisitViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.medicalrecord.service.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

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
    public String listVisits(Model model) {
        List<VisitDto> visitDtos = visitService.getAllVisits();
        List<VisitViewModel> visits = visitDtos.stream().map(dto -> mapperUtil.map(dto, VisitViewModel.class)).collect(Collectors.toList());
        model.addAttribute("visits", visits);
        return "visits/visits-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {


        model.addAttribute("visit", new CreateVisitViewModel());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnosis());
        model.addAttribute("treatments",treatmentService.getAllTreatments());
        model.addAttribute("sickLeaves",sickLeaveService.getAllSickLeaves());
        return "visits/visits-create";
    }

    @PostMapping("/create")
    public String createVisit(@ModelAttribute("visit") CreateVisitViewModel visitViewModel) {
        CreateVisitDto dto = mapperUtil.map(visitViewModel, CreateVisitDto.class);
        visitService.createVisit(dto);
        return "redirect:/visits";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        VisitDto dto = visitService.getVisitById(id);
        CreateVisitViewModel viewModel = new CreateVisitViewModel();
        viewModel.setId(dto.getId());

        viewModel.setDoctorId(dto.getDoctor().getId());
        viewModel.setPatientId(dto.getPatient().getId());
        viewModel.setLocalDate(dto.getLocalDate());
        viewModel.setDiagnosisId(dto.getDiagnosis().getId());
        viewModel.setTreatmentId(dto.getTreatment().getId());
        viewModel.setSickLeaveId(dto.getSickLeave().getId());

        model.addAttribute("visit", viewModel);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("diagnoses", diagnosisService.getAllDiagnosis());
        model.addAttribute("treatments",treatmentService.getAllTreatments());
        model.addAttribute("sickLeaves",sickLeaveService.getAllSickLeaves());
        return "visits/visits-edit";
    }

    @PostMapping("/edit")
    public String editVisit(@ModelAttribute("visit") CreateVisitViewModel viewModel) {
        CreateVisitDto dto = mapperUtil.map(viewModel, CreateVisitDto.class);
        visitService.updateVisit(viewModel.getId(), dto);
        return "redirect:/visits";
    }

    @GetMapping("/delete/{id}")
    public String deleteVisit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            visitService.deleteVisit(id);
            redirectAttributes.addFlashAttribute("successMessage", "Visit deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/visits";
    }

    @GetMapping("/archived")
    public String showArchivedVisits(Model model) {
        List<VisitDto> deleted = visitService.getAllDeletedVisits();
        List<VisitViewModel> viewModels = deleted.stream()
                .map(dto -> mapperUtil.map(dto, VisitViewModel.class))
                .collect(Collectors.toList());
        model.addAttribute("visits", viewModels);
        return "visits/archived-visits";
    }

    @PostMapping("/restore/{id}")
    public String restoreVisit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        visitService.restoreVisit(id);
        redirectAttributes.addFlashAttribute("successMessage", "Visit restored successfully.");
        return "redirect:/visits";
    }
}

