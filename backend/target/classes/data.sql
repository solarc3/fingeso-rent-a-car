-- Sucursales
INSERT INTO sucursales (nombre, direccion, telefono, email)
VALUES ('Sucursal Santiago', 'Usach 123', '56933174228', 'martin.fuentes.r@rentacar.cl');
INSERT INTO sucursales (nombre, direccion, telefono, email)
VALUES ('Sucursal Copiapo', 'Copiapo 123', '56948893000', 'solar@rentacar.cl');

-- Vehiculos
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('CHEVROLET', 'GROOVE', 'SCMD', 'ASKS01', 2024, 100000.00, 1, true, 'DISPONIBLE');
INSERT INTO vehiculos (marca, modelo, acriss, patente, anio, precio_arriendo, sucursal_id, disponible, estado)
VALUES ('TOYOTA', 'COROLLA', 'RCAD', 'ABCD01', 2020, 90000.00, 1, true, 'EN_REPARACION');

-- Usuarios
INSERT INTO usuarios (rut, nombre, apellido, esta_en_lista_negra, sucursal_id)
VALUES ('24150030-3', 'Ignacio', 'Solar', false, 1);
INSERT INTO usuarios (rut, nombre, apellido, esta_en_lista_negra, sucursal_id)
VALUES ('14353454-1', 'Diego', 'Gomez', false, 1);
INSERT INTO usuarios (rut, nombre, apellido, esta_en_lista_negra, sucursal_id)
VALUES ('00000000-0', 'Felipe', 'Cubillos', true, 1);

-- Reservas
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-09 23:58:00', '2025-01-06 00:00:00', 100000.00, 1, 1, 1, 1);
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id)
VALUES ('2024-12-09 23:57:00', '2024-01-05 00:00:00', 90000.00, 2, 2, 2, 1);

-- Valoraciones
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (5, 'Excelente vehiculo, muy cómodo', 1, 1);
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id)
VALUES (2, 'Vehiculo presenta falla', 2, 2);