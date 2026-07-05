package com.anahuac;

import com.anahuac.domain.Vehicle;
import com.anahuac.service.VehicleService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

    @Inject
    VehicleService vehicleService;

    @GET
    @Timed(value = "vehicles.list.timer")
    public List<Vehicle> list() {
        return vehicleService.listAll();
    }

    @GET
    @Path("/available")
    public List<Vehicle> available() {
        return vehicleService.findAvailable();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        Vehicle vehicle = vehicleService.findById(id);
        if (vehicle == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(vehicle).build();
    }

    @POST
    @Counted(value = "vehicles.created.count")
    public Response create(@Valid Vehicle vehicle) {
        Vehicle created = vehicleService.create(vehicle);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}/availability/{available}")
    public Response changeAvailability(@PathParam("id") Long id,
                                        @PathParam("available") boolean available) {
        Vehicle vehicle = vehicleService.changeAvailability(id, available);
        if (vehicle == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(vehicle).build();
    }

    /**
     * Endpoint reactivo (Mutiny) — devuelve vehículos disponibles
     * ordenados por capacidad, sin bloquear el event loop.
     */
    @GET
    @Path("/reactive/recommendations")
    public Uni<List<Vehicle>> recommendations() {
        return vehicleService.recommendedVehicles();
    }
}
