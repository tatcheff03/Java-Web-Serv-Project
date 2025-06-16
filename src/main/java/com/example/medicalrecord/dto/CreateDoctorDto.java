package com.example.medicalrecord.dto;

import com.example.medicalrecord.data.Enum.Specialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class CreateDoctorDto {

    @NotBlank(message = "Doctor name cannot be blank")
    private  String doctorName;

    private boolean personalDoc;
    @Size(min=1, message = "At least one specialization is required")
    private Set<Specialization> specializations;
    @NotBlank(message = "Username cannot be blank")
    private String username;
}
