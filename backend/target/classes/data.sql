-- Sucursales
INSERT INTO sucursales (nombre, direccion, telefono, email)
VALUES ('Sucursal Santiago', 'Usach 123', '56933174228', 'martin.fuentes.r@rentacar.cl');
INSERT INTO sucursales (nombre, direccion, telefono, email)
VALUES ('Sucursal Copiapo', 'Copiapo 123', '56948893000', 'solar@rentacar.cl');
INSERT INTO sucursales (nombre, direccion, telefono, email)
VALUES ('Sucursal Viña del Mar', 'Libertad 456', '56942345678', 'vina@rentacar.cl');
INSERT INTO sucursales (nombre, direccion, telefono, email)
VALUES ('Sucursal Concepción', 'Barros Arana 789', '56987654321', 'concepcion@rentacar.cl');
INSERT INTO sucursales (nombre, direccion, telefono, email)
VALUES ('Sucursal Antofagasta', 'Baquedano 234', '56912345678', 'antofagasta@rentacar.cl');

-- Vehiculos
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('CHEVROLET', 'GROOVE', 'SCMD', 'ASKS01', 2024, 100000.00, 1, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('TOYOTA', 'COROLLA', 'RCAD', 'ABCD01', 2020, 90000.00, 1, true, 'EN_REPARACION');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('NISSAN', 'VERSA', 'ECMR', 'BBJK22', 2023, 85000.00, 2, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('HYUNDAI', 'ACCENT', 'ECMR', 'CCKL33', 2022, 80000.00, 2, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('KIA', 'SPORTAGE', 'SFAR', 'DDMN44', 2023, 120000.00, 3, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('SUZUKI', 'SWIFT', 'EDMR', 'EEPQ55', 2024, 75000.00, 3, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('VOLKSWAGEN', 'GOLF', 'CDMR', 'FFRS66', 2023, 95000.00, 4, true, 'EN_REPARACION');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('MAZDA', 'CX-5', 'SFAR', 'GGTU77', 2024, 130000.00, 4, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('HONDA', 'CIVIC', 'CDMR', 'HHVW88', 2023, 98000.00, 5, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('FORD', 'ESCAPE', 'SFAR', 'IIXY99', 2024, 125000.00, 5, true, 'DISPONIBLE');
-- Añadiendo más vehículos (manteniendo los anteriores)
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES
-- Sedanes Económicos
('TOYOTA', 'YARIS', 'ECMR', 'JJAB11', 2023, 65000.00, 1, true, 'DISPONIBLE'),
('CHEVROLET', 'SAIL', 'ECMR', 'KKCD22', 2023, 60000.00, 2, true, 'DISPONIBLE'),
('NISSAN', 'V-DRIVE', 'ECMR', 'LLEF33', 2023, 62000.00, 3, true, 'DISPONIBLE'),
('KIA', 'RIO', 'ECMR', 'MMGH44', 2024, 68000.00, 4, true, 'DISPONIBLE'),
('HYUNDAI', 'GRAND I10', 'ECMR', 'NNIJ55', 2024, 63000.00, 5, true, 'DISPONIBLE'),
-- Sedanes Intermedios
('TOYOTA', 'COROLLA CROSS', 'ICMR', 'PPKL66', 2024, 95000.00, 1, true, 'DISPONIBLE'),
('VOLKSWAGEN', 'VIRTUS', 'ICMR', 'QQMN77', 2024, 88000.00, 2, true, 'DISPONIBLE'),
('CHEVROLET', 'ONIX', 'ICMR', 'RRPQ88', 2023, 85000.00, 3, true, 'DISPONIBLE'),
('NISSAN', 'SENTRA', 'ICMR', 'SSRS99', 2024, 92000.00, 4, true, 'DISPONIBLE'),
('KIA', 'CERATO', 'ICMR', 'TTTU10', 2023, 90000.00, 5, true, 'DISPONIBLE'),
-- SUVs Compactas
('TOYOTA', 'RAV4', 'CFAR', 'UUVW11', 2024, 120000.00, 1, true, 'DISPONIBLE'),
('NISSAN', 'QASHQAI', 'CFAR', 'VVXY12', 2024, 115000.00, 2, true, 'DISPONIBLE'),
('MAZDA', 'CX-30', 'CFAR', 'WWZA13', 2023, 110000.00, 3, true, 'DISPONIBLE'),
('HYUNDAI', 'TUCSON', 'CFAR', 'XXAB14', 2024, 118000.00, 4, true, 'DISPONIBLE'),
('KIA', 'SELTOS', 'CFAR', 'YYCD15', 2023, 112000.00, 5, true, 'DISPONIBLE'),

-- SUVs Medianas
('TOYOTA', 'FORTUNER', 'IFAR', 'ZZEF16', 2024, 135000.00, 1, true, 'DISPONIBLE'),
('HONDA', 'CR-V', 'IFAR', 'AAGH17', 2024, 130000.00, 2, true, 'DISPONIBLE'),
('NISSAN', 'X-TRAIL', 'IFAR', 'BBIJ18', 2023, 128000.00, 3, true, 'DISPONIBLE'),
('MAZDA', 'CX-5', 'IFAR', 'CCKL19', 2024, 132000.00, 4, true, 'DISPONIBLE'),
('HYUNDAI', 'SANTA FE', 'IFAR', 'DDMN20', 2023, 134000.00, 5, true, 'DISPONIBLE'),

-- Camionetas
('TOYOTA', 'HILUX', 'PFAR', 'EEPQ21', 2024, 145000.00, 1, true, 'DISPONIBLE'),
('NISSAN', 'NAVARA', 'PFAR', 'FFRS22', 2024, 142000.00, 2, true, 'DISPONIBLE'),
('MITSUBISHI', 'L200', 'PFAR', 'GGTU23', 2023, 140000.00, 3, true, 'DISPONIBLE'),
('CHEVROLET', 'COLORADO', 'PFAR', 'HHVW24', 2024, 143000.00, 4, true, 'DISPONIBLE'),
('FORD', 'RANGER', 'PFAR', 'IIXY25', 2023, 141000.00, 5, true, 'DISPONIBLE'),

-- Vehículos Deportivos
('FORD', 'MUSTANG', 'XDAR', 'JJZA26', 2024, 180000.00, 1, true, 'DISPONIBLE'),
('CHEVROLET', 'CAMARO', 'XDAR', 'KKAB27', 2023, 175000.00, 2, true, 'DISPONIBLE'),
('DODGE', 'CHALLENGER', 'XDAR', 'LLCD28', 2024, 178000.00, 3, true, 'DISPONIBLE'),

-- Vehículos de Lujo
('BMW', 'SERIE 3', 'LDAR', 'MMEF29', 2024, 200000.00, 1, true, 'DISPONIBLE'),
('MERCEDES-BENZ', 'CLASE C', 'LDAR', 'NNGH30', 2024, 210000.00, 2, true, 'DISPONIBLE'),
('AUDI', 'A4', 'LDAR', 'OOIJ31', 2024, 205000.00, 3, true, 'DISPONIBLE'),

-- Vehículos Híbridos/Eléctricos
('TOYOTA', 'PRIUS', 'HDAR', 'PPKL32', 2024, 110000.00, 1, true, 'DISPONIBLE'),
('HYUNDAI', 'IONIQ', 'EDAR', 'QQMN33', 2024, 125000.00, 2, true, 'DISPONIBLE'),
('KIA', 'NIRO', 'HDAR', 'RRPQ34', 2024, 115000.00, 3, true, 'DISPONIBLE'),

-- Minivans
('KIA', 'CARNIVAL', 'MVAR', 'SSRS35', 2024, 150000.00, 1, true, 'DISPONIBLE'),
('HYUNDAI', 'STARIA', 'MVAR', 'TTTU36', 2024, 155000.00, 2, true, 'DISPONIBLE'),
('PEUGEOT', 'EXPERT', 'MVAR', 'UUVW37', 2023, 145000.00, 3, true, 'DISPONIBLE');
-- Usuarios
INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('21284189-7', 'Ignacio', 'Solar', 'admin123', false, 1, 'ADMINISTRADOR');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('14353454-5', 'Diego', 'Gomez', 'worker123', false, 1, 'TRABAJADOR');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('00000000-0', 'Felipe', 'Cubillos', 'client123', true, 1, 'ARRENDATARIO');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('15789456-0', 'Ana', 'Martinez', 'worker123', false, 2, 'TRABAJADOR');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('16852147-2', 'Carlos', 'Rodriguez', 'client123', false, 2, 'ARRENDATARIO');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('17963258-6', 'Maria', 'López', 'worker123', false, 3, 'TRABAJADOR');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('18741852-6', 'Juan', 'Pérez', 'client123', true, 3, 'ARRENDATARIO');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('19852963-K', 'Patricia', 'González', 'worker123', false, 4, 'TRABAJADOR');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('20147258-K', 'Roberto', 'Sánchez', 'client123', false, 4, 'ARRENDATARIO');

INSERT INTO usuarios (rut, nombre, apellido, password, esta_en_lista_negra, sucursal_id, rol)
VALUES ('21369852-4', 'Carmen', 'Muñoz', 'worker123', false, 5, 'TRABAJADOR');

-- Reservas
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-09 23:58:00', '2025-01-06 00:00:00', 100000.00, 'PENDIENTE', 1, 1, 1);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-09 23:57:00', '2024-01-05 00:00:00', 90000.00, 'CONFIRMADA', 2, 2, 1);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-15 10:00:00', '2024-12-20 10:00:00', 85000.00, 'PENDIENTE', 4, 3, 2);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-16 15:00:00', '2024-12-23 15:00:00', 120000.00, 'PENDIENTE', 5, 5, 2);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-20 09:00:00', '2024-12-27 09:00:00', 75000.00, 'PENDIENTE', 6, 6, 3);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-22 14:00:00', '2024-12-29 14:00:00', 95000.00, 'CONFIRMADA', 8, 7, 4);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-25 11:00:00', '2025-01-01 11:00:00', 130000.00, 'PENDIENTE', 9, 8, 4);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-28 16:00:00', '2025-01-04 16:00:00', 98000.00, 'PENDIENTE', 10, 9, 5);

-- Valoraciones
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (5, 'Excelente vehiculo, muy cómodo', 1, 1);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (2, 'Vehiculo presenta falla', 2, 2);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (4, 'Muy buen servicio, auto en excelentes condiciones', 4, 3);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (5, 'Perfecto para viajes familiares', 5, 5);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (3, 'Regular, podría mejorar la limpieza', 6, 6);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (4, 'Buena relación precio-calidad', 8, 7);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (5, 'Excelente SUV, muy espacioso', 9, 8);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (4, 'Buen rendimiento en carretera', 10, 9);