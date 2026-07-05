# Vehicle Reservation Cloud Native — Reto 5

Microservicio Quarkus de reservación de vehículos. Cubre las 10 tareas del reto:
REST, CDI, Panache, Mutiny, gRPC, Health Checks, Métricas, GraalVM Native Image, Docker, Kubernetes con Kind.

> **Nota**: el PDF original del reto tenía varias páginas sin texto extraíble (probablemente
> capturas de pantalla). Este proyecto reconstruye el código visible (VehicleResource,
> VehicleGrpcServiceImpl, comandos) y completa lo faltante (entidad Reservation, health checks,
> métricas, manifiestos de Kubernetes/Kind) con supuestos razonables — ajusta según tu enunciado real.

## 1. Requisitos previos

- JDK 17
- Maven (o el `./mvnw` incluido al generar el proyecto con code.quarkus.io)
- Docker
- Kind (`go install sigs.k8s.io/kind@latest` o binario oficial)
- kubectl
- PostgreSQL corriendo localmente en `localhost:5432` (o ajusta `application.properties`)
- [HTTPie](https://httpie.io/) (`http`) — opcional, se puede usar `curl`

## 2. Levantar Postgres local rápido (opcional, con Docker)

```bash
docker run -d --name vehicle-db \
  -e POSTGRES_USER=vehicle_user \
  -e POSTGRES_PASSWORD=vehicle_pass \
  -e POSTGRES_DB=vehicle_db \
  -p 5432:5432 postgres:16
```

## 3. Modo desarrollo

```bash
cd vehicle-reservation-cloudnative
./mvnw quarkus:dev
```

Dev UI: http://localhost:18080/q/dev

## 4. Build JVM y ejecución

```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

Probar health:
```bash
http GET :18080/q/health
```

## 5. Build nativo (GraalVM)

Local (requiere GraalVM instalado):
```bash
./mvnw clean package -Dnative
./target/vehicle-reservation-cloudnative-1.0.0-runner
```

Usando contenedor de build (no requiere GraalVM local):
```bash
./mvnw clean package -Dnative -Dquarkus.native.container-build=true
```

```bash
http GET :18080/q/health
```

## 6. Docker — imagen JVM

```bash
./mvnw clean package
docker build -f src/main/docker/Dockerfile.jvm \
  -t vehicle-reservation-cloudnative:jvm .
docker run --rm -p 18080:18080 vehicle-reservation-cloudnative:jvm
```

## 7. Docker — imagen nativa

```bash
./mvnw clean package -Dnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native \
  -t vehicle-reservation-cloudnative:native .
docker run --rm -p 18080:18080 vehicle-reservation-cloudnative:native
```

## 8. Kubernetes con Kind

```bash
# Crear el cluster
kind create cluster --config k8s/kind-config.yaml

# Cargar la imagen construida localmente dentro del cluster
kind load docker-image vehicle-reservation-cloudnative:native --name vehicle-reservation-cluster

# Desplegar
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# Verificar
kubectl get pods
kubectl get svc

# Probar (el service NodePort está mapeado a localhost:18080 vía kind-config.yaml)
http GET :18080/q/health
```

Para borrar el cluster:
```bash
kind delete cluster --name vehicle-reservation-cluster
```

## 9. Probar REST y Mutiny

```bash
http GET :18080/vehicles
http GET :18080/vehicles/available
http GET :18080/vehicles/1
http POST :18080/vehicles plate=NEW111 type=SEDAN capacity:=5 available:=true
http PUT :18080/vehicles/1/availability/false
http GET :18080/vehicles/reactive/recommendations

http POST :18080/reservations vehicleId:=2 customerName=Jordy startDate=2026-07-10 endDate=2026-07-12
http PUT :18080/reservations/1/confirm
```

## 10. Probar gRPC

Puerto gRPC: `9000` (definido en `application.properties`, `quarkus.grpc.server.port`).

Con [grpcurl](https://github.com/fullstorydev/grpcurl):
```bash
grpcurl -plaintext -d '{"id": 1}' localhost:9000 vehicle.VehicleGrpcService/GetVehicle
```

## 11. Health checks y métricas

```bash
http GET :18080/q/health
http GET :18080/q/health/live
http GET :18080/q/health/ready
http GET :18080/q/metrics
http GET :18080/q/swagger-ui
```

## 12. Postman

Importa `postman/Vehicle-Reservation-CloudNative.postman_collection.json`.
Incluye ejemplos de request/response para vehicles, reservations y los endpoints de
observabilidad (health/metrics/openapi).

## Estructura del proyecto

```
vehicle-reservation-cloudnative/
├── pom.xml
├── src/main/java/com/anahuac/
│   ├── VehicleResource.java
│   ├── ReservationResource.java
│   ├── domain/ (Vehicle, Reservation, ReservationStatus)
│   ├── service/ (VehicleService, ReservationService)
│   ├── grpc/VehicleGrpcServiceImpl.java
│   └── health/ (LivenessCheck, VehicleServiceHealthCheck)
├── src/main/proto/vehicle.proto
├── src/main/resources/ (application.properties, import.sql)
├── src/main/docker/ (Dockerfile.jvm, Dockerfile.native)
├── k8s/ (kind-config.yaml, deployment.yaml, service.yaml)
└── postman/Vehicle-Reservation-CloudNative.postman_collection.json
```
