package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<SucursalEntity, Long> {

    Optional<SucursalEntity> findByVehiculosContains(VehiculoEntity vehiculo);

}
