package com.rentacar.backend.services;

import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.repositories.ReservaRepository;
import com.rentacar.backend.repositories.SucursalRepository;
import com.rentacar.backend.repositories.UsuarioRepository;
import com.rentacar.backend.repositories.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final VehiculoRepository vehiculoRepository;




    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository,
                          SucursalRepository sucursalRepository, VehiculoRepository vehiculoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.sucursalRepository = sucursalRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    List<ReservaEntity> obtenerUsuario(UsuarioEntity usuario) {return reservaRepository.findByUsuario(usuario);}

    List<ReservaEntity> obtenerVehiculo(VehiculoEntity vehiculo) {return reservaRepository.findByVehiculo(vehiculo);}

    List<ReservaEntity> obtenerSucursal(SucursalEntity sucursal) {return reservaRepository.findBySucursal(sucursal);}


    ReservaEntity crearReserva(LocalDateTime fechaInicio, LocalDateTime fechaFin, BigDecimal costo,
                               Integer estado,Long usuarioID, Long vehiculoID, Long sucursalID) {
        ReservaEntity reserva = new ReservaEntity();
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setCosto(costo);
        reserva.setEstado(estado);
        reserva.setUsuario(usuarioRepository.findById(usuarioID).orElseThrow());
        reserva.setVehiculo(vehiculoRepository.findById(vehiculoID).orElseThrow());
        reserva.setSucursal(sucursalRepository.findById(sucursalID).orElseThrow());
        return reservaRepository.save(reserva);
        }


}
