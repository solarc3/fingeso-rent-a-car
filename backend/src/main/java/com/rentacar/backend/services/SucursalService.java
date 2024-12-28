package com.rentacar.backend.services;

import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rentacar.backend.repositories.SucursalRepository;
import com.rentacar.backend.repositories.VehiculoRepository;
import com.rentacar.backend.repositories.UsuarioRepository;
import com.rentacar.backend.repositories.ReservaRepository;

import java.util.List;
import java.util.Optional;


@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ReservaRepository reservaRepository;

    @Autowired
    public SucursalService(SucursalRepository sucursalRepository,
                           UsuarioRepository usuarioRepository,
                           VehiculoRepository vehiculoRepository,
                           ReservaRepository reservaRepository) {
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.reservaRepository = reservaRepository;
    }

    public SucursalEntity crearSucursal(String nombre, String direccion, String telefono, String email) {
        SucursalEntity sucursal = new SucursalEntity();
        sucursal.setNombre(nombre);
        sucursal.setDireccion(direccion);
        sucursal.setTelefono(telefono);
        sucursal.setEmail(email);
        return sucursalRepository.save(sucursal);
    }

    public SucursalEntity obtenerSucursalPorId(Long id) {
        Optional<SucursalEntity> sucursal = sucursalRepository.findById(id);
        if (sucursal.isPresent()) {
            return sucursal.get();
        } else {
            throw new RuntimeException("No se encontro la sucursal");
        }
    }
    public List<SucursalEntity> obtenerSucursales(){
        return sucursalRepository.findAll();
    }

    public SucursalEntity agregarVehiculo(Long vehiculoID, Long sucursalID) {
        SucursalEntity sucursal = sucursalRepository.findById(sucursalID).orElseThrow(()-> new RuntimeException(
                "No se encontro sucursal de Id: " + sucursalID));
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoID).orElseThrow(()-> new RuntimeException(
                "No se encontro empleado de Id" + vehiculoID));
        sucursal.getVehiculos().add(vehiculo);

        return sucursalRepository.save(sucursal);
    }


    public SucursalEntity agregarEmpleado(Long empleadoID, Long sucursalID) {
        SucursalEntity sucursal = sucursalRepository.findById(sucursalID).orElseThrow(()-> new RuntimeException(
                "No se encontro sucursal de Id: " + sucursalID));
        UsuarioEntity empleado = usuarioRepository.findById(empleadoID).orElseThrow(()-> new RuntimeException(
                "No se encontro empleado de Id" + empleadoID));
        sucursal.getEmpleados().add(empleado);

        return sucursalRepository.save(sucursal);

    }

    public SucursalEntity actualizarSucursal(Long id, String nombre, String direccion, String telefono, String email)
    throws Exception {
        SucursalEntity sucursal = obtenerSucursalPorId(id);
        sucursal.setNombre(nombre);
        sucursal.setDireccion(direccion);
        sucursal.setTelefono(telefono);
        sucursal.setEmail(email);
        return sucursalRepository.save(sucursal);
    }

    public void eliminarSucursalPorId(Long id) throws Exception {
        if (!sucursalRepository.existsById(id)) {
            throw new Exception("Sucursal no encontrada con Id: " + id);
        }
        sucursalRepository.deleteById(id);
    }

    public SucursalEntity encontrarSucursal(VehiculoEntity vehiculo) {
        Optional<SucursalEntity> sucursal = sucursalRepository.findByVehiculosContains(vehiculo);
        if (sucursal.isEmpty()) {
            throw new RuntimeException("El vehiculo no tiene sucursal asignada");
        }
        return sucursal.get();
    }

}