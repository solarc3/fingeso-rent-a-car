package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.entities.SucursalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<VehiculoEntity, Long> {

    Optional<VehiculoEntity> findByPatente(String patente);

    List<VehiculoEntity> findByMarca(String marca);

    List<VehiculoEntity> findByAcrissLike(String acriss);

    //List<VehiculoEntity> findBySucursal(SucursalEntity sucursal);

    List<VehiculoEntity> findByPrecioArriendoBetween(BigDecimal min, BigDecimal max);

    List<VehiculoEntity> findByEstado(VehiculoEntity.EstadoVehiculo estado);

    @Query("SELECT DISTINCT v.marca FROM VehiculoEntity v ORDER BY v.marca")
    List<String> findDistinctMarcas();


   // @Query("SELECT v FROM VehiculoEntity v LEFT JOIN FETCH v.sucursal")
    //List<VehiculoEntity> findAllWithSucursales();


}
