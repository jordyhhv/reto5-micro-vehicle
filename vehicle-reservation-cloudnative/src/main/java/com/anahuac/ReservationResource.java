package com.anahuac;

import com.anahuac.domain.Reservation;
import com.anahuac.service.ReservationService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    ReservationService reservationService;

    @GET
    public List<Reservation> list() {
        return reservationService.listAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Reservation reservation = reservationService.findById(id);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reservation).build();
    }

    @POST
    public Response create(@Valid Reservation reservation) {
        Reservation created = reservationService.create(reservation);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}/confirm")
    public Response confirm(@PathParam("id") Long id) {
        Reservation reservation = reservationService.confirm(id);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reservation).build();
    }

    @PUT
    @Path("/{id}/cancel")
    public Response cancel(@PathParam("id") Long id) {
        Reservation reservation = reservationService.cancel(id);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reservation).build();
    }

    @PUT
    @Path("/{id}/complete")
    public Response complete(@PathParam("id") Long id) {
        Reservation reservation = reservationService.complete(id);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reservation).build();
    }
}
