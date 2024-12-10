package com.rentacar.backend.repositories;

import com.rentacar.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<SucursalEntity, Long> {

    //Metodo para encontrar sucursal por nombre
    List<SucursalEntity> findByNombre(String nombre);

    List<SucursalEntity> findByDireccion(String direccion);

    //Metodo para encontrar sucursal por telefono
    List<SucursalEntity> findByTelefono(String telefono);

    //Metodo para encontrar sucursal por email
    List<SucursalEntity> findByEmail(String email);

    // Buscar todos los empleados de una sucursal
    List<SucursalEntity> findsByEmpleado(UsuarioEntity empleado);

    // Buscar todos los vehículos de una sucursal
    List<SucursalEntity> findByVehiculo(VehiculoEntity vehiculo);

    // Buscar todas las reservas de una sucursal
    List<SucursalEntity> findByReservas(ReservaEntity reservas);
}
