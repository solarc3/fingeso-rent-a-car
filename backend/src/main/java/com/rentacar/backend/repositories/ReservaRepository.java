package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.entities.SucursalEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    // buscar por estado
    List<ReservaEntity> findByEstado(ReservaEntity.EstadoReserva Estado);

    // buscar todas las reservas de un usuario
    List<ReservaEntity> findByUsuario(UsuarioEntity usuario);

    // buscar todas las reservas de un vehiculo
    List<ReservaEntity> findByVehiculo(VehiculoEntity vehiculo);

    // buscar todas las reservas de una sucursal
    List<ReservaEntity> findBySucursal(SucursalEntity sucursal);

    @Query("SELECT r FROM ReservaEntity r WHERE r.vehiculo.id = :vehiculoId " +
            "AND ((r.fechaInicio BETWEEN :fechaInicio AND :fechaFin) OR " +
            "(r.fechaFin BETWEEN :fechaInicio AND :fechaFin))")
    List<ReservaEntity> findByVehiculoAndFechasSuperpuestas(
            Long vehiculoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    List<ReservaEntity> findByVehiculoAndEstado(VehiculoEntity vehiculo, ReservaEntity.EstadoReserva estado);

    List<ReservaEntity> findByFechaInicioBetweenAndEstadoIn(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            List<ReservaEntity.EstadoReserva> estados
    );
}
