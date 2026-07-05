package com.anahuac.grpc;

import com.anahuac.domain.Vehicle;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

@GrpcService
@Blocking
public class VehicleGrpcServiceImpl implements VehicleGrpcService {

    @Override
    public Uni<VehicleReply> getVehicle(VehicleRequest request) {
        Vehicle vehicle = Vehicle.findById(request.getId());

        if (vehicle == null) {
            return Uni.createFrom().item(
                    VehicleReply.newBuilder()
                            .setId(request.getId())
                            .setPlate("NOT_FOUND")
                            .setType("UNKNOWN")
                            .setCapacity(0)
                            .setAvailable(false)
                            .build()
            );
        }

        return Uni.createFrom().item(
                VehicleReply.newBuilder()
                        .setId(vehicle.id)
                        .setPlate(vehicle.plate)
                        .setType(vehicle.type)
                        .setCapacity(vehicle.capacity)
                        .setAvailable(vehicle.available)
                        .build()
        );
    }
}
