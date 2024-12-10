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

-- Sucursales
INSERT INTO sucursales (nombre, direccion, telefono, email) VALUES
('Sucursal Santiago', 'Usach 123', '56933174228', 'martin.fuentes.r@rentacar.cl'),
('Sucursal Copiapo', 'Copiapo 123', '56948893000', 'solar@rentacar.cl');

-- Vehiculos
INSERT INTO vehiculos (marca, modelo, patente, anio, precio_arriendo, sucursal_id, disponible, estado) VALUES
('CHEVROLET', 'GROOVE', 'ASKsdd', 2024, 100000, 21, TRUE, 'DISPONIBLE' ), --por alguna razon empieza desde 21
('TOYOTA', 'COROLLA', 'ABCddd', 2020, 90000, 22, TRUE, 'EN_REPARACION');

-- Usuarios
INSERT INTO usuarios (rut, nombre, apellido, esta_en_lista_negra, sucursal_id) VALUES
('24150030-3', 'Ignacio', 'Solar', FALSE, 1),
('14353454-1', 'Diego', 'Gomez', FALSE, 2),
('00000000-0', 'Felipe', 'Cubillos', TRUE, 2);

-- Reservas
INSERT INTO reservas (fecha_inicio, fecha_fin, costo, estado, usuario_id, vehiculo_id, sucursal_id) VALUES
('2024-12-9 23:58', '2025-1-6', 100000, 1, 1, 1, 1), --CONFIRMADA
('2024-12-9 23:57', '2024-1-5', 90000, 2, 2, 2, 2); --EN PROGRESO

-- Valoraciones
INSERT INTO valoraciones (puntuacion, comentario, usuario_id, vehiculo_id) VALUES
(5, 'Excelente vehiculo, muy cómodo', 1, 1),
(2, 'Vehiculo presenta falla', 2, 2);





