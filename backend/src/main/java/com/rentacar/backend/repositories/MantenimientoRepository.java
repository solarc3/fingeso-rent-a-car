package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.MantenimientoEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<MantenimientoEntity, Long> {
    List<MantenimientoEntity> findByVehiculo(VehiculoEntity vehiculo);

    List<MantenimientoEntity> findByFechaRealizadaBetween(LocalDateTime parse, LocalDateTime parse1);
}