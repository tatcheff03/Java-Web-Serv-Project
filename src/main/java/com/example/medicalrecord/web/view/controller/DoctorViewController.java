package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.Exceptions.SoftDeleteException;
import com.example.medicalrecord.data.Enum.Specialization;

import com.example.medicalrecord.dto.CreateDoctorDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.web.view.controller.model.DoctorViewModel;
import com.example.medicalrecord.web.view.controller.model.CreateDoctorViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorViewController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final MapperUtil mapperUtil;

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String listDoctors(Model model) {
        List<DoctorDto> doctorDtos = doctorService.getAllDoctors();
        List<DoctorViewModel> doctors = doctorDtos.stream().map(dto -> mapperUtil.map(dto, DoctorViewModel.class)).toList();
        model.addAttribute("doctors", doctors);
        return "doctor/doctorlist";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("doctor", new CreateDoctorViewModel());
        model.addAttribute("specializations", Specialization.values());
        return "doctor/doctorcreate";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@ModelAttribute("doctor") CreateDoctorViewModel createModel) {
        CreateDoctorDto dto = mapperUtil.map(createModel, CreateDoctorDto.class);
        doctorService.createDoctor(dto);
        return "redirect:/doctors";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        DoctorDto dto = doctorService.getDoctorById(id);
        CreateDoctorViewModel viewModel = mapperUtil.map(dto, CreateDoctorViewModel.class);
        model.addAttribute("doctor", viewModel);
        model.addAttribute("specializations", Specialization.values());

        return "doctor/doctoredit";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@ModelAttribute("doctor") CreateDoctorViewModel updateModel) {
        CreateDoctorDto dto = mapperUtil.map(updateModel, CreateDoctorDto.class);
        doctorService.updateDoctor(updateModel.getId(), dto);
        return "redirect:/doctors";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Doctor deleted successfully.");
        } catch (SoftDeleteException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error: " + e.getMessage());
        }

        return "redirect:/doctors";
    }

    @GetMapping("/archived")
    @PreAuthorize("hasRole('ADMIN')")
    public String showArchivedDoctors(Model model) {

        List<DoctorViewModel> archived = doctorService.getAllDeletedDoctors().stream().map(dto -> mapperUtil.map(dto, DoctorViewModel.class)).toList();
        model.addAttribute("doctors", archived);
        return "doctor/archived-doctor-list";
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showDoctorPatientCountReport(Model model, Authentication authentication) {
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        if (isDoctor) {
            return "redirect:/unauthorized?from=/doctors/report";
        }

        List<DoctorDto> allDoctors = doctorService.getAllDoctors();

        allDoctors.forEach(doctor -> {
            int count = patientService.getPatientsByDoctorId(doctor.getId()).size();
            doctor.setPatientCount(count);
        });

        model.addAttribute("doctors", allDoctors);
        return "doctor/doctor-patients-count";
    }


    @GetMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String restoreDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        doctorService.restoreDoctor(id);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor restored successfully.");
        return "redirect:/doctors";
    }
}