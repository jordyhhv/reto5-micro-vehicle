package com.anahuac.service;

import com.anahuac.domain.Vehicle;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class VehicleService {

    public List<Vehicle> listAll() {
        return Vehicle.listAll();
    }

    public List<Vehicle> findAvailable() {
        return Vehicle.list("available", true);
    }

    public Vehicle findById(Long id) {
        return Vehicle.findById(id);
    }

    @Transactional
    public Vehicle create(Vehicle vehicle) {
        vehicle.id = null;
        vehicle.persist();
        return vehicle;
    }

    @Transactional
    public Vehicle changeAvailability(Long id, boolean available) {
        Vehicle vehicle = Vehicle.findById(id);
        if (vehicle == null) {
            return null;
        }
        vehicle.available = available;
        return vehicle;
    }

    /**
     * Endpoint reactivo (Mutiny): calcula "recomendaciones" de vehículos
     * disponibles ordenados por capacidad descendente, ejecutando el trabajo
     * bloqueante de JPA en el worker pool para no bloquear el event loop.
     */
    public Uni<List<Vehicle>> recommendedVehicles() {
        return Uni.createFrom().item(() ->
                        Vehicle.<Vehicle>list("available", true).stream()
                                .sorted(Comparator.comparingInt((Vehicle v) -> v.capacity).reversed())
                                .toList()
                )
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}
