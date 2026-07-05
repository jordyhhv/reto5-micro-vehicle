package com.anahuac.health;

import com.anahuac.domain.Vehicle;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
public class VehicleServiceHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        try {
            long count = Vehicle.count();
            return HealthCheckResponse.named("vehicle-database-connection")
                    .up()
                    .withData("vehicles", count)
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("vehicle-database-connection")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}
