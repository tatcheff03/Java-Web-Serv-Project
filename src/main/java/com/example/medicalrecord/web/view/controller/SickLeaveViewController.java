package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.dto.CreateSickLeaveDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.dto.PatientDto;
import com.example.medicalrecord.dto.SickLeaveDto;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.service.SickLeaveService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.CreateSickLeaveViewModel;
import com.example.medicalrecord.web.view.controller.model.SickLeaveViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveViewController {

    private final SickLeaveService sickLeaveService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final MapperUtil mapperUtil;

    @GetMapping
    public String showAllSickLeaves(Model model) {
        List<SickLeaveDto> dtos = sickLeaveService.getAllSickLeaves();
        List<SickLeaveViewModel> viewModels = dtos.stream()
                .map(dto -> mapperUtil.map(dto, SickLeaveViewModel.class))
                .collect(Collectors.toList());

        model.addAttribute("sickLeaves", viewModels);
        return "sick-leaves/sick-leaves-list";
    }

    @GetMapping("/create")
    public String showCreateSickLeaveForm(Model model) {
        model.addAttribute("sickLeave", new CreateSickLeaveViewModel());

        List<PatientDto> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        List<DoctorDto> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        return "sick-leaves/create-sick-leave";
    }

    @PostMapping("/create")
    public String createSickLeave(@ModelAttribute("sickLeave") CreateSickLeaveViewModel viewModel) {
        CreateSickLeaveDto dto = mapperUtil.map(viewModel, CreateSickLeaveDto.class);
        sickLeaveService.createSickLeave(dto);
        return "redirect:/sick-leaves";
    }

    @GetMapping("/edit/{id}")
    public String editSickLeaveForm(@PathVariable Long id, Model model) {
        SickLeaveDto sickLeaveDto = sickLeaveService.getSickLeaveById(id);
        CreateSickLeaveViewModel viewModel = mapperUtil.map(sickLeaveDto, CreateSickLeaveViewModel.class);
        model.addAttribute("sickLeave", viewModel);

        List<PatientDto> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        List<DoctorDto> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        return "sick-leaves/edit-sick-leave";
    }

    @PostMapping("/edit")
    public String submitSickLeave(@ModelAttribute("sickLeave") CreateSickLeaveViewModel viewModel) {
        CreateSickLeaveDto dto = mapperUtil.map(viewModel, CreateSickLeaveDto.class);
        sickLeaveService.updateSickLeave(viewModel.getId(), dto);
        return "redirect:/sick-leaves";
    }

    @GetMapping("/delete/{id}")
    public String deleteSickLeave(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sickLeaveService.deleteSickLeave(id);
            redirectAttributes.addFlashAttribute("successMessage", "Sick leave deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sick-leaves";
    }

}


