package com.example.medicalrecord.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "sick_leave")
public class SickLeave extends BaseEntity{

    private LocalDate startDate;
    private int dayDuration;

    @ManyToOne
    @JoinColumn(name = "issued_by_id", nullable = false)
    private Doctor issuedBy;
}
