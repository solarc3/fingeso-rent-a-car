-- Sucursales

INSERT INTO sucursales (nombre, direccion, telefono, email) VALUES
("Sucursal Santiago", "Usach 123", "56933174228", "martin.fuentes.r@rentacar.cl"),
("Sucursal Copiapo", "Copiapo 123", "56948893000", "solar@rentacar.cl");

-- Vehiculos

INSERT INTO vehiculos (marca, modelo, patente, anio, precio_arriendo, sucursal_id, disponibilidad, estado) VALUES
("CHEVROLET", "GROOVE", "ASK123", "2024", "100000", 1, TRUE, "DISPONIBLE" ),
("TOYOTA", "COROLLA", "ABC123", "2020", "90000", 2, TRUE, "EN_REPARACION");

-- Usuarios
INSERT INTO usuarios (rut, nombre, apellido, esta_en_lista_negra, sucursal_id) VALUES
("12345678-9", "Ignacio", "Solar", FALSE, 1),
("12012024-3", "Diego", "Gomez", FALSE, 2),
("00000000-0", "Felipe", "Cubillos", TRUE, 2);

-- Reservas
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id) VALUES
("2024-12-9 23:58", "2025-1-6", "100000", 1, 1, 1, 1), --Pendiente
("2024-12-9 23:57", "20244-1-5", "90000", 2, 2, 2, 2); --Confirmada

-- Valoraciones
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id) VALUES
(5, "Excelente vehiculo, muy cómodo", 1, 1),
(2, "Vehiculo presenta falla", 2, 2);


-- Se reinician las secuencias para evitar conflictos
SET session_replication_role = 'replica';
DO $$
DECLARE
_table text;
    _schema text := current_schema();
BEGIN
FOR _table IN
SELECT quote_ident(tablename)
FROM pg_tables
WHERE schemaname = _schema
  AND tablename NOT LIKE 'pg_%'
  AND tablename NOT LIKE 'sql_%'
    LOOP
        -- eliminar datos
        EXECUTE format('TRUNCATE TABLE %s CASCADE', _table);
END LOOP;
    -- reiniciar secuencias
FOR _table IN
SELECT sequence_name
FROM information_schema.sequences
WHERE sequence_schema = _schema
    LOOP
    EXECUTE format('ALTER SEQUENCE %s RESTART WITH 1', quote_ident(_table));
END LOOP;
END $$;


