package com.example.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AttachSickLeaveDto {
    @NotNull(message = "Sick Leave ID cannot be null")
    private Long sickLeaveId;
}
