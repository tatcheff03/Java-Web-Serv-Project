package com.example.medicalrecord.web.view.controller;

import com.example.medicalrecord.data.entity.Doctor;
import com.example.medicalrecord.dto.CreateSickLeaveDto;
import com.example.medicalrecord.dto.DoctorDto;
import com.example.medicalrecord.dto.PatientDto;
import com.example.medicalrecord.dto.SickLeaveDto;
import com.example.medicalrecord.service.DoctorService;
import com.example.medicalrecord.service.PatientService;
import com.example.medicalrecord.service.SickLeaveService;
import com.example.medicalrecord.util.MapperUtil;
import com.example.medicalrecord.util.OwnershipUtil;
import com.example.medicalrecord.web.view.controller.model.CreateSickLeaveViewModel;
import com.example.medicalrecord.web.view.controller.model.SickLeaveViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveViewController {

    private final SickLeaveService sickLeaveService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final MapperUtil mapperUtil;

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @GetMapping
    public String showAllSickLeaves(Model model) {
        List<SickLeaveDto> dtos = sickLeaveService.getAllSickLeaves();
        List<SickLeaveViewModel> viewModels = dtos.stream().map(dto -> mapperUtil.map(dto, SickLeaveViewModel.class)).collect(Collectors.toList());

        model.addAttribute("sickLeaves", viewModels);
        return "sick-leaves/sick-leaves-list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public String showCreateSickLeaveForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<PatientDto> patients;
        Long doctorId;
        String doctorName;


        DoctorDto currentDoctor = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctorId = currentDoctor.getId();
        doctorName = currentDoctor.getDoctorName();

        if (isDoctor && !isAdmin) {
            patients = patientService.getAllActivePatients().stream()
                    .filter(p -> p.getPersonalDoctor().getId().equals(doctorId))
                    .toList();
        } else {
            patients = patientService.getAllActivePatients();
        }

        CreateSickLeaveViewModel viewModel = new CreateSickLeaveViewModel();
        viewModel.setIssuedById(doctorId);

        model.addAttribute("sickLeave", viewModel);
        model.addAttribute("doctorName", doctorName);
        model.addAttribute("patients", patients);

        return "sick-leaves/create-sick-leave";
    }




    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String createSickLeave(@ModelAttribute("sickLeave") CreateSickLeaveViewModel viewModel, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long doctorId;

        if (isDoctor && !isAdmin) {
            doctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"))
                    .getId();
        } else {


            doctorId = viewModel.getIssuedById();

            if (doctorId == null) {
                throw new IllegalArgumentException("Doctor ID is required for ADMIN users.");
            }
        }



        CreateSickLeaveDto dto = mapperUtil.map(viewModel, CreateSickLeaveDto.class);
        dto.setIssuedById(viewModel.getIssuedById());
        sickLeaveService.createSickLeave(dto);
        return "redirect:/sick-leaves";
    }



    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String editSickLeaveForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        DoctorDto doctor = doctorService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor (admin/doctor) not found"));

        SickLeaveDto sickLeaveDto = sickLeaveService.getSickLeaveById(id);


        if (isDoctor && !isAdmin) {
            OwnershipUtil.verifyOwnership(
                    sickLeaveDto.getIssuedBy().getId(),
                    doctor.getId(),
                    "You cannot edit another doctor's sick leave.");
        }

        CreateSickLeaveViewModel viewModel = mapperUtil.map(sickLeaveDto, CreateSickLeaveViewModel.class);
        viewModel.setIssuedById(doctor.getId());
        model.addAttribute("sickLeave", viewModel);


        List<PatientDto> patients = isDoctor && !isAdmin
                ? patientService.getPatientsByDoctorId(doctor.getId())
                : patientService.getAllActivePatients();

        model.addAttribute("patients", patients);
        model.addAttribute("doctorName", doctor.getDoctorName());
        viewModel.setId(sickLeaveDto.getId());
        return "sick-leaves/edit-sick-leave";
    }


    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String submitSickLeave(@ModelAttribute("sickLeave") CreateSickLeaveViewModel viewModel, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();

        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long doctorId;

        if (isDoctor && !isAdmin) {
            doctorId = doctorService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"))
                    .getId();
        } else {
            doctorId = viewModel.getIssuedById();
            if (doctorId == null) {
                throw new IllegalArgumentException("Doctor ID is required for ADMIN users.");
            }
        }


        viewModel.setIssuedById(doctorId);

        CreateSickLeaveDto dto = mapperUtil.map(viewModel, CreateSickLeaveDto.class);
        dto.setIssuedById(doctorId);

        sickLeaveService.updateSickLeave(viewModel.getId(), dto);
        return "redirect:/sick-leaves";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String deleteSickLeave(@PathVariable Long id, RedirectAttributes redirectAttributes,  Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        DoctorDto doctor = doctorService.findByUsername(username).orElseThrow(() -> new RuntimeException("Doctor not found"));

        SickLeaveDto sickLeaveDto = sickLeaveService.getSickLeaveById(id);


        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        if (isDoctor) {
            OwnershipUtil.verifyOwnership(
                    sickLeaveDto.getIssuedBy().getId(),
                    doctor.getId(),
                    "You cannot delete another doctor's sick leave.");
        }


        try {
            sickLeaveService.deleteSickLeave(id);
            redirectAttributes.addFlashAttribute("successMessage", "Sick leave deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/sick-leaves";
    }


    @GetMapping("/archived")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String showArchivedSickLeaves(Model model, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        Long doctorId = doctorService.findByUsername(username).orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

        List<SickLeaveDto> deleted = sickLeaveService.getAllDeletedSickLeaves();

        // само тези на текущия доктор
        List<SickLeaveDto> ownDeleted = deleted.stream().filter(sl -> sl.getIssuedBy().getId().equals(doctorId)).toList();

        List<SickLeaveViewModel> viewModels = ownDeleted.stream().map(dto -> mapperUtil.map(dto, SickLeaveViewModel.class)).toList();

        model.addAttribute("sickLeaves", viewModels);
        return "sick-leaves/archived-sick-leaves";
    }


    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public String viewMySickLeaves(Model model, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        Long doctorId = doctorService.findByUsername(username).orElseThrow(() -> new RuntimeException("Doctor not found")).getId();

        List<SickLeaveDto> sickLeaves = sickLeaveService.getAllSickLeavesByDoctorId(doctorId);

        List<SickLeaveViewModel> viewModels = sickLeaves.stream().map(dto -> {
            SickLeaveViewModel vm = new SickLeaveViewModel();
            vm.setStartDate(dto.getStartDate());
            vm.setEndDate(dto.getStartDate().plusDays(dto.getDayDuration()));
            vm.setPatientName(dto.getPatient().getName());
            return vm;
        }).toList();

        model.addAttribute("sickLeaves", viewModels);
        return "doctor/doc-sickleaves";
    }


    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public String restoreSickLeave(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication authentication) {
        String username = ((OidcUser) authentication.getPrincipal()).getPreferredUsername();
        DoctorDto doctor = doctorService.findByUsername(username).orElseThrow(() -> new RuntimeException("Doctor not found"));

        SickLeaveDto sickLeaveDto = sickLeaveService.getSickLeaveById(id);

        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        if (isDoctor) {
            OwnershipUtil.verifyOwnership(
                    sickLeaveDto.getIssuedBy().getId(),
                    doctor.getId(),
                    "You cannot restore another doctor's sick leave.");
        }

        sickLeaveService.restoreSickLeave(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sick leave restored successfully.");
        return "redirect:/sick-leaves";
    }

    @GetMapping("/report/most-sick-month")
    @PreAuthorize("hasRole('ADMIN')")
    public String showMonthWithMostSickLeaves(Model model) {
        var entry = sickLeaveService.getMonthWithMostSickLeaves();
        model.addAttribute("topMonth", entry != null ? entry.getKey() : null);
        model.addAttribute("count", entry != null ? entry.getValue() : 0);
        return "sick-leaves/most-issued-month";
    }

    @GetMapping("/report/top-doctors-by-sick-leaves")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDoctorsWithMostSickLeaves(Model model) {
        Map<Doctor, Long> topDoctors = sickLeaveService.getDoctorsWithMostSickLeaves();
        model.addAttribute("topDoctors", topDoctors);
        return "sick-leaves/top-doctors-by-sickleaves";
    }



}



