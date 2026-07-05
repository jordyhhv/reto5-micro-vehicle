package com.anahuac.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Reservation extends PanacheEntity {

    @NotNull
    public Long vehicleId;

    @NotBlank
    public String customerName;

    @NotNull
    public LocalDate startDate;

    @NotNull
    public LocalDate endDate;

    @Enumerated(EnumType.STRING)
    public ReservationStatus status = ReservationStatus.PENDING;

    public LocalDateTime createdAt = LocalDateTime.now();

    public Reservation() {
    }

    public Reservation(Long vehicleId, String customerName, LocalDate startDate, LocalDate endDate) {
        this.vehicleId = vehicleId;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
