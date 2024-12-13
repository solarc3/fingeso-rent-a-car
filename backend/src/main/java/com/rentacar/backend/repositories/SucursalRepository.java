package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<SucursalEntity, Long> {

    //Metodo para encontrar sucursal por nombre
    Optional<SucursalEntity> findByNombre(String nombre);

    Optional<SucursalEntity> findByDireccion(String direccion);

    //Metodo para encontrar sucursal por telefono
    List<SucursalEntity> findByTelefono(String telefono);

    //Metodo para encontrar sucursal por email
    List<SucursalEntity> findByEmail(String email);

    // Buscar todos los empleados de una sucursal
    //List<SucursalEntity> findByEmpleados(UsuarioEntity empleado);

    // Buscar todos los vehículos de una sucursal
    //List<SucursalEntity> findByVehiculos(VehiculoEntity vehiculo);

    // Buscar todas las reservas de una sucursal
    //List<SucursalEntity> findByReservas(ReservaEntity reservas);
}
