package com.anahuac.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
public class Vehicle extends PanacheEntity {

    @NotBlank
    public String plate;

    @NotBlank
    public String type; // SEDAN, SUV, VAN, TRUCK

    @Positive
    public int capacity;

    public boolean available = true;

    public Vehicle() {
    }

    public Vehicle(String plate, String type, int capacity, boolean available) {
        this.plate = plate;
        this.type = type;
        this.capacity = capacity;
        this.available = available;
    }
}
