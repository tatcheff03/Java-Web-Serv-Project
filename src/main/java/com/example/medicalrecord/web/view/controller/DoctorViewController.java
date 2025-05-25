package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.data.Enum.Specialization;
import com.example.medicalrecord.dto.CreateDoctorDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.DoctorViewModel;
import com.example.medicalrecord.web.view.controller.model.CreateDoctorViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;



@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorViewController {

    private final DoctorService doctorService;
    private final MapperUtil mapperUtil;

    @GetMapping
    public String listDoctors(Model model) {
        List<DoctorDto> doctorDtos = doctorService.getAllDoctors();
        List<DoctorViewModel> doctors = doctorDtos.stream()
                .map(dto -> mapperUtil.map(dto, DoctorViewModel.class))
                .toList();
        model.addAttribute("doctors", doctors);
        return "doctor/doctorlist";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("doctor", new CreateDoctorViewModel());
        model.addAttribute("specializations", Specialization.values());
        return "doctor/doctorcreate";
    }

    @PostMapping
    public String create(@ModelAttribute("doctor") CreateDoctorViewModel createModel) {
        CreateDoctorDto dto = mapperUtil.map(createModel, CreateDoctorDto.class);
        doctorService.createDoctor(dto);
        return "redirect:/doctors";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        DoctorDto dto = doctorService.getDoctorById(id);
        CreateDoctorViewModel viewModel = mapperUtil.map(dto, CreateDoctorViewModel.class);
        model.addAttribute("doctor", viewModel);
        model.addAttribute("specializations", Specialization.values());
        model.addAttribute("id", id);
        return "doctor/doctoredit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("doctor") CreateDoctorViewModel updateModel) {
        CreateDoctorDto dto = mapperUtil.map(updateModel, CreateDoctorDto.class);
        doctorService.updateDoctor(id, dto);
        return "redirect:/doctors";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctors";
    }
}
