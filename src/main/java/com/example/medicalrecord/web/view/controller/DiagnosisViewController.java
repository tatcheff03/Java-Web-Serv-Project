package com.example.medicalrecord.web.view.controller;

import org.springframework.ui.Model;
import com.example.medicalrecord.service.DiagnosisService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.CreateDiagnosisViewModel;
import com.example.medicalrecord.web.view.controller.model.DiagnosisViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.medicalrecord.dto.*;

import java.util.List;

@Controller
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class DiagnosisViewController {

    private final DiagnosisService diagnosisService;
    private final MapperUtil mapperUtil;

    @GetMapping
    public String listDiagnoses(Model model) {
        List<DiagnosisDto> diagnosisDtos = diagnosisService.getAllDiagnosis();
        List<DiagnosisViewModel> diagnosis = diagnosisDtos.stream()
                .map(dto -> mapperUtil.map(dto, DiagnosisViewModel.class))
                .toList();
        model.addAttribute("diagnoses", diagnosis);
        return "diagnosis/diagnosislist";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("diagnosis", new CreateDiagnosisViewModel());
        return "diagnosis/diagnosiscreate";
    }

    @PostMapping
    public String create(@ModelAttribute("diagnosis") CreateDiagnosisViewModel createModel) {
        CreateDiagnosisDto dto = mapperUtil.map(createModel, CreateDiagnosisDto.class);
        diagnosisService.createDiagnosis(dto);
        return "redirect:/diagnoses";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        DiagnosisDto dto = diagnosisService.getDiagnosisById(id);
        CreateDiagnosisViewModel viewModel = mapperUtil.map(dto, CreateDiagnosisViewModel.class);
        model.addAttribute("diagnosis", viewModel);
        model.addAttribute("id", id);
        return "diagnosis/diagnosisedit";
    }

    @PostMapping("/edit/{id}")
    public String updateDiagnosis(@PathVariable Long id,
                                  @ModelAttribute("diagnosis") CreateDiagnosisViewModel updateModel) {
        DiagnosisDto dto = mapperUtil.map(updateModel, DiagnosisDto.class);
        dto.setId(id);
        diagnosisService.updateDiagnosis(id, dto);
        return "redirect:/diagnoses";
    }


    @GetMapping("/delete/{id}")
    public String deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return "redirect:/diagnoses";
    }
}
