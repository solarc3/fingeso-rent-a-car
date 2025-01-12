package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByRut(String rut);

    //List<UsuarioEntity> findByEstaEnListaNegra(boolean estaEnListaNegra);

    List<UsuarioEntity> findBysoftDelete(boolean softDelete);

    //List<UsuarioEntity> findByValoracionesContaining(ValoracionEntity valoracion);

    //List<UsuarioEntity> findByValoracionesIn(List<ValoracionEntity> valoraciones);

    //List<UsuarioEntity> findByReservasContaining(ReservaEntity reserva);

    //List<UsuarioEntity> findByReservasIn(List<ReservaEntity> reservas);

    //List<UsuarioEntity> findBySucursal(SucursalEntity sucursal);

    List<UsuarioEntity> findByRol(UsuarioEntity.RolUsuario rol);
}