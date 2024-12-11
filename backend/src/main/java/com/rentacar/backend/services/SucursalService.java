package com.rentacar.backend.services;

import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rentacar.backend.repositories.SucursalRepository;
import com.rentacar.backend.repositories.VehiculoRepository;
import com.rentacar.backend.repositories.UsuarioRepository;
import com.rentacar.backend.repositories.ReservaRepository;

import java.util.List;


@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ReservaRepository reservaRepository;

    public SucursalService(SucursalService sucursalService) {
        this.sucursalRepository = sucursalService.sucursalRepository;
        this.usuarioRepository = sucursalService.usuarioRepository;
        this.vehiculoRepository = sucursalService.vehiculoRepository;
        this.reservaRepository = sucursalService.reservaRepository;
    }

    List<SucursalEntity> obtenerVehiculo(VehiculoEntity vehiculo) {
        return sucursalRepository.findByVehiculo(vehiculo);
    }

    SucursalEntity creaSucursal(String nombre, String direccion, String telefono, String email) {
        SucursalEntity sucursal = new SucursalEntity();
        sucursal.setNombre(nombre);
        sucursal.setDireccion(direccion);
        sucursal.setTelefono(telefono);
        sucursal.setEmail(email);
        return sucursalRepository.save(sucursal);
    }
    SucursalEntity agregarVehiculo(Long vehiculoID, SucursalEntity sucursal) {
        sucursal.getVehiculos().add(vehiculoRepository.findById(vehiculoID).get());
        return sucursalRepository.save(sucursal);
    }
    SucursalEntity agregarEmpleado(Long empleadoID, SucursalEntity sucursal) {
        sucursal.getReservas().add(reservaRepository.findById(empleadoID).get());
        return sucursalRepository.save(sucursal);
    }

}