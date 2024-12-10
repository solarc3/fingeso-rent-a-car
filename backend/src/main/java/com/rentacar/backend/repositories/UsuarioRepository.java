package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByRut(String rut);

    List<UsuarioEntity> findByEstaEnListaNegra(boolean estaEnListaNegra);

    List<UsuarioEntity> findByValoraciones(ValoracionEntity valoraciones);

    List<UsuarioEntity> findByReservas(ReservaEntity reservas);

    List<UsuarioEntity> findBySucursal(SucursalEntity sucursal);


}
