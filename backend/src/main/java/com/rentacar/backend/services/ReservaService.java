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

import java.time.Duration;
import java.time.Period;
import java.util.NoSuchElementException;
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

    public List<ReservaEntity> obtenerReservas() {
        return reservaRepository.findAll();
    }

    public List<ReservaEntity> obtenerReservasDeUsuario(UsuarioEntity usuario) {
        return reservaRepository.findByUsuario(usuario);
    }

    public List<ReservaEntity> obtenerReservasDeVehiculo(VehiculoEntity vehiculo) {
        return reservaRepository.findByVehiculo(vehiculo);
    }

    public List<ReservaEntity> obtenerReservasDeSucursal(SucursalEntity sucursal) {
        return reservaRepository.findBySucursal(sucursal);
    }

    public ReservaEntity extenderReserva(Long reservaId, LocalDateTime nuevaFechaFin) {
        Optional<ReservaEntity> reserva = reservaRepository.findById(reservaId);
        if (reserva.isEmpty())
            throw new NoSuchElementException("Reserva no encontrada");

        // Comprobar periodo 30 dias
        ReservaEntity r = reserva.get();
        Duration treintaDias = Duration.ofDays(30);
        Duration periodoArriendo = Duration.between(r.getFechaFin(), nuevaFechaFin);

        if (periodoArriendo.compareTo(treintaDias) > 0)
            throw new RuntimeException("Periodo superior a 30 dias");

        r.setFechaFin(nuevaFechaFin);
        return reservaRepository.save(r);
    }

    public ReservaEntity crearReserva(LocalDateTime fechaInicio, LocalDateTime fechaFin, BigDecimal costo,
                               Integer estado, Long usuarioID, Long vehiculoID, Long sucursalID) {
        // Comprobar periodo 30 dias
        Duration treintaDias = Duration.ofDays(30);
        Duration periodoArriendo = Duration.between(fechaInicio, fechaFin);

        if (periodoArriendo.compareTo(treintaDias) > 0)
            throw new RuntimeException("Periodo superior a 30 dias");

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
