package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.entities.SucursalEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long>{

    //Buscar por fecha inicio
    List<ReservaEntity> findByFechaInicio(LocalDateTime fechaInicio);

    //Buscar por fecha final
    List<ReservaEntity> findByFechaFin(LocalDateTime fechaFinal);

    //Buscar entre dos fechas
    //List<ReservaEntity> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFinal);

    //Buscar por costo
    List<ReservaEntity> findByCosto(BigDecimal costo);

    //Buscar entre dos costos
    List<ReservaEntity> findByCostoBetween(BigDecimal costoMin, BigDecimal costoMax);

    //Buscar por estado
    List<ReservaEntity> findByEstado(Integer Estado);

    // Buscar todas las reservas de un usuario
    List<ReservaEntity> findByUsuario(UsuarioEntity usuario);

    // Buscar todas las reservas de un vehículo
    List<ReservaEntity> findByVehiculo(VehiculoEntity vehiculo);

    // Buscar todas las reservas de una sucursal
    List<ReservaEntity> findBySucursal(SucursalEntity sucursal);

}
