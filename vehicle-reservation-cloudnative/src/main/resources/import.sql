INSERT INTO Vehicle(id, plate, type, capacity, available) VALUES (1, 'ABC123', 'SEDAN', 4, true);
INSERT INTO Vehicle(id, plate, type, capacity, available) VALUES (2, 'XYZ789', 'SUV', 6, true);
INSERT INTO Vehicle(id, plate, type, capacity, available) VALUES (3, 'VAN456', 'VAN', 12, false);
ALTER SEQUENCE vehicle_seq RESTART WITH 4;
