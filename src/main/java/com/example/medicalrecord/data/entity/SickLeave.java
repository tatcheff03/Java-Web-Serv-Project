package com.example.medicalrecord.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "sick_leave")
public class SickLeave extends BaseEntity {
    @NotNull
    @PastOrPresent
    private LocalDate startDate;

    @Positive
    @Max(365)
    private int dayDuration;

    @ManyToOne
    @NotNull
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_by_id", nullable = false)
    @NotNull
    private Doctor issuedBy;

    @Column(nullable = false)
    private boolean deleted = false;
}
