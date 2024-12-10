package com.rentacar.backend.services;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {
    @Autowired
    private VehiculoRepository vehiculoRepository;

    VehiculoEntity crearVehiculo(VehiculoEntity vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    Optional<VehiculoEntity> obtenerVehiculoPorPatente(String patente) {
        return vehiculoRepository.findByPatente(patente);
    }

    List<VehiculoEntity> obtenerVehiculosPorMarca(String marca) {
        return vehiculoRepository.findByMarca(marca);
    }

    List<VehiculoEntity> obtenerVehiculosPorSucursal(SucursalEntity sucursal) {
        return vehiculoRepository.findBySucursal(sucursal);
    }

    List<VehiculoEntity> obtenerVehiculosConPrecioEntre(BigDecimal min, BigDecimal max) {
        return vehiculoRepository.findByPrecioArriendoBetween(min, max);
    }

    List<VehiculoEntity> obtenerVehiculosPorEstado(String estado) {
        return vehiculoRepository.findByEstado(estado);
    }

    VehiculoEntity actualizarEstadoVehiculoPorId(Long vehiculoId, String estado) {
        VehiculoEntity vehiculo = vehiculoRepository.findById(vehiculoId).orElseThrow();
        vehiculo.setEstado(estado);
        return vehiculoRepository.save(vehiculo);
    }

    void eliminarVehiculoPorId(Long vehiculoId) {
        vehiculoRepository.findById(vehiculoId).orElseThrow();
        vehiculoRepository.deleteById(vehiculoId);
    }
}
