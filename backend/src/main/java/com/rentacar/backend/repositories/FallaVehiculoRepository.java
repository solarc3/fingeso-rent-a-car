package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.FallaVehiculoEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FallaVehiculoRepository extends JpaRepository<FallaVehiculoEntity, Long> {
    List<FallaVehiculoEntity> findByVehiculoOrderByFechaReporteDesc(VehiculoEntity vehiculo);
}