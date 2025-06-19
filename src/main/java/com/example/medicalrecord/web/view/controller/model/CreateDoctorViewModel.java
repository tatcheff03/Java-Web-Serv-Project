package com.example.medicalrecord.web.view.controller.model;

import com.example.medicalrecord.data.Enum.Specialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateDoctorViewModel {
    private Long id;
    @NotBlank(message = "Doctor name is required.")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
    private String doctorName;
    private boolean personalDoc;
    @NotEmpty(message = "At least one specialization must be selected.")
    private Set<Specialization> specializations;
    @NotBlank(message = "Username is required.")
    private String username;
    @NotBlank(message = "Unique identifier is required.")
    private String uniqueIdentifier;
}
