package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.dto.CreateMedicineDto;
import com.example.medicalrecord.dto.MedicineDto;
import com.example.medicalrecord.service.MedicineService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.CreateMedicineViewModel;
import com.example.medicalrecord.web.view.controller.model.MedicineViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String allMedicines(Model model) {


        List<MedicineDto> medicineDtos = medicineService.getAllMedicines();
        List<MedicineViewModel> medicines = medicineDtos.stream().map(dto -> mapperUtil.map(dto, MedicineViewModel.class)).collect(Collectors.toList());
        model.addAttribute("medicines", medicines);
        return "medicines/medicine-list";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String editMedicineForm(@PathVariable Long id, Model model) {


        MedicineDto medicineDto = medicineService.getMedicineById(id);
        MedicineViewModel viewModel = mapperUtil.map(medicineDto, MedicineViewModel.class);
        model.addAttribute("medicine", viewModel);
        return "medicines/edit-medicine";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showCreateMedicineForm(Model model) {


        model.addAttribute("medicine", new CreateMedicineViewModel());
        return "medicines/create-medicine";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String createMedicine(@ModelAttribute("medicine") CreateMedicineViewModel viewModel) {


        CreateMedicineDto dto = mapperUtil.map(viewModel, CreateMedicineDto.class);
        medicineService.createMedicine(dto);
        return "redirect:/medicines";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String editMedicineSubmit(@ModelAttribute("medicine") MedicineViewModel viewModel) {


        MedicineDto dto = mapperUtil.map(viewModel, MedicineDto.class);
        medicineService.updateMedicine(dto);
        return "redirect:/medicines";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String deleteMedicine(@PathVariable Long id, RedirectAttributes redirectAttributes) {


        try {
            medicineService.deleteMedicine(id);
            redirectAttributes.addFlashAttribute("successMessage", "Medicine deleted successfully.");
        } catch (SoftDeleteException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
        }

        return "redirect:/medicines";
    }


    @GetMapping("/archived")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showArchivedMedicines(Model model) {
        List<MedicineDto> archived = medicineService.getAllArchivedMedicines(); // нов метод
        List<MedicineViewModel> viewModels = archived.stream().map(dto -> mapperUtil.map(dto, MedicineViewModel.class)).collect(Collectors.toList());
        model.addAttribute("archived", viewModels);
        return "medicines/archived-medicines";
    }

    @GetMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String restoreMedicine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            medicineService.restoreMedicine(id);
            redirectAttributes.addFlashAttribute("successMessage", "Medicine restored successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/medicines";
    }

}