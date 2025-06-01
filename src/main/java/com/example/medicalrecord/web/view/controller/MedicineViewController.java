package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.service.MedicineService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.CreateMedicineViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import com.example.medicalrecord.dto.*;
import org.springframework.ui.Model;
import com.example.medicalrecord.web.view.controller.model.MedicineViewModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicineViewController {
    private final MedicineService medicineService;
    private final MapperUtil mapperUtil;


    @GetMapping
    public String allMedicines(Model model) {
        List<MedicineDto> medicineDtos = medicineService.getAllMedicines();
        List<MedicineViewModel> medicines = medicineDtos.stream()
                .map(dto -> mapperUtil.map(dto, MedicineViewModel.class))
                .collect(Collectors.toList());
        model.addAttribute("medicines", medicines);
        return "medicines/medicine-list";
    }

    @GetMapping("/edit/{id}")
    public String editMedicineForm(@PathVariable Long id, Model model) {
        MedicineDto medicineDto = medicineService.getMedicineById(id);
        MedicineViewModel viewModel = mapperUtil.map(medicineDto, MedicineViewModel.class);
        model.addAttribute("medicine", viewModel);
        return "medicines/edit-medicine";
    }

    @GetMapping("/create")
    public String showCreateMedicineForm(Model model) {
        model.addAttribute("medicine", new CreateMedicineViewModel());
        return "medicines/create-medicine";
    }

    @PostMapping("/create")
    public String createMedicine(@ModelAttribute("medicine") CreateMedicineViewModel viewModel) {
        CreateMedicineDto dto = mapperUtil.map(viewModel, CreateMedicineDto.class);
        medicineService.createMedicine(dto);
        return "redirect:/medicines";
    }


    @PostMapping("/edit")
    public String editMedicineSubmit(@ModelAttribute("medicine") MedicineViewModel viewModel) {
        MedicineDto dto = mapperUtil.map(viewModel, MedicineDto.class);
        medicineService.updateMedicine(dto);
        return "redirect:/medicines";
    }

    @GetMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return "redirect:/medicines";
    }


}
