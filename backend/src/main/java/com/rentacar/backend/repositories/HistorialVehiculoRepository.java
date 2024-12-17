package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.HistorialVehiculoEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialVehiculoRepository extends JpaRepository<HistorialVehiculoEntity, Long> {
    List<HistorialVehiculoEntity> findByVehiculoOrderByFechaDesc(VehiculoEntity vehiculo);
}