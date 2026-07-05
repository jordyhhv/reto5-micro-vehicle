package com.anahuac.service;

import com.anahuac.domain.Reservation;
import com.anahuac.domain.ReservationStatus;
import com.anahuac.domain.Vehicle;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class ReservationService {

    @Inject
    VehicleService vehicleService;

    public List<Reservation> listAll() {
        return Reservation.listAll();
    }

    public Reservation findById(Long id) {
        return Reservation.findById(id);
    }

    @Transactional
    public Reservation create(Reservation reservation) {
        Vehicle vehicle = vehicleService.findById(reservation.vehicleId);
        if (vehicle == null) {
            throw new WebApplicationException("Vehicle not found: " + reservation.vehicleId,
                    Response.Status.NOT_FOUND);
        }
        if (!vehicle.available) {
            throw new WebApplicationException("Vehicle " + vehicle.id + " is not available",
                    Response.Status.CONFLICT);
        }
        if (reservation.endDate.isBefore(reservation.startDate)) {
            throw new WebApplicationException("endDate must not be before startDate",
                    Response.Status.BAD_REQUEST);
        }

        reservation.id = null;
        reservation.status = ReservationStatus.PENDING;
        reservation.persist();
        return reservation;
    }

    @Transactional
    public Reservation confirm(Long id) {
        Reservation reservation = Reservation.findById(id);
        if (reservation == null) return null;

        Vehicle vehicle = vehicleService.findById(reservation.vehicleId);
        if (vehicle != null) {
            vehicle.available = false;
        }
        reservation.status = ReservationStatus.CONFIRMED;
        return reservation;
    }

    @Transactional
    public Reservation cancel(Long id) {
        Reservation reservation = Reservation.findById(id);
        if (reservation == null) return null;

        Vehicle vehicle = vehicleService.findById(reservation.vehicleId);
        if (vehicle != null) {
            vehicle.available = true;
        }
        reservation.status = ReservationStatus.CANCELLED;
        return reservation;
    }

    @Transactional
    public Reservation complete(Long id) {
        Reservation reservation = Reservation.findById(id);
        if (reservation == null) return null;

        Vehicle vehicle = vehicleService.findById(reservation.vehicleId);
        if (vehicle != null) {
            vehicle.available = true;
        }
        reservation.status = ReservationStatus.COMPLETED;
        return reservation;
    }
}
