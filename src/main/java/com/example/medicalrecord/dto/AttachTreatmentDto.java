package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AttachTreatmentDto {
    @NotNull(message = "Treatment ID cannot be null")
    private Long treatmentId;
}
