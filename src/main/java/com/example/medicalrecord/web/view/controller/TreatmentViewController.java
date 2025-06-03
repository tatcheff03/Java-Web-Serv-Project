package com.example.medicalrecord.web.view.controller;


import com.example.medicalrecord.dto.CreateTreatmentDto;
import com.example.medicalrecord.dto.TreatmentDto;
import com.example.medicalrecord.service.MedicineService;
import com.example.medicalrecord.service.TreatmentService;
import com.example.medicalrecord.web.view.controller.model.CreateTreatmentViewModel;
import com.example.medicalrecord.web.view.controller.model.TreatmentViewModel;
import com.example.medicalrecord.dto.*;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/treatment")
@RequiredArgsConstructor
public class TreatmentViewController {

    private final TreatmentService treatmentService;
    private final MedicineService medicineService;



    @GetMapping
    public String viewAllTreatments(Model model) {
        List<TreatmentDto> treatments = treatmentService.getAllTreatments();

        List<TreatmentViewModel> viewModels = treatments.stream().map(dto -> {
            TreatmentViewModel vm = new TreatmentViewModel();
            vm.setId(dto.getId());
            vm.setInstructions(dto.getInstructions());
            vm.setMedications(dto.getMedications().stream()
                    .map(MedicineDto::getMedicineName)
                    .toList());
            return vm;
        }).toList();

        model.addAttribute("treatments", viewModels);
        return "treatment/treatments-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("treatment", new CreateTreatmentViewModel());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "treatment/treatments-create";
    }

    @PostMapping("/create")
    public String createTreatment(@ModelAttribute("treatment") CreateTreatmentViewModel viewModel) {
        CreateTreatmentDto dto = new CreateTreatmentDto();
        dto.setInstructions(viewModel.getInstructions());
        dto.setMedicationIds(viewModel.getMedicationIds());
        treatmentService.createTreatment(dto);
        return "redirect:/treatment";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TreatmentDto treatment = treatmentService.getTreatmentById(id);
        CreateTreatmentViewModel viewModel = new CreateTreatmentViewModel();
        viewModel.setInstructions(treatment.getInstructions());
        viewModel.setMedicationIds(treatment.getMedications().stream()
                .map(MedicineDto::getId)
                .collect(Collectors.toSet()));

        model.addAttribute("treatment", viewModel);
        model.addAttribute("treatmentId", id);
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "treatment/treatments-edit";
    }

    @PostMapping("/edit/{id}")
    public String editTreatment(@PathVariable Long id, @ModelAttribute("treatment") CreateTreatmentViewModel viewModel) {
        CreateTreatmentDto dto = new CreateTreatmentDto();
        dto.setInstructions(viewModel.getInstructions());
        dto.setMedicationIds(viewModel.getMedicationIds());
        treatmentService.updateTreatment(id, dto);
        return "redirect:/treatment";
    }

    @GetMapping("/delete/{id}")
    public String deleteTreatment(@PathVariable Long id) {
        treatmentService.deleteTreatment(id);
        return "redirect:/treatment";
    }
}
