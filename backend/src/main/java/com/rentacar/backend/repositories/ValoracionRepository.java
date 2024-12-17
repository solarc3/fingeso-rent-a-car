package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.ValoracionEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ValoracionRepository extends JpaRepository<ValoracionEntity, Long> {

    // esto se me hace algo sus, pero lo dejare a criterio
    Optional<ValoracionEntity> findById(Long id);

    // un findByComentario no tiene sentido

    List<ValoracionEntity> findByVehiculo(VehiculoEntity vehiculoEntity);
}
