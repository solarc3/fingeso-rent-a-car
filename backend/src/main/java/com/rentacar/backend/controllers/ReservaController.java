package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.services.ReservaService;
import com.rentacar.backend.services.UsuarioService;
import com.rentacar.backend.services.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reserva")
public class ReservaController {
    private final ReservaService reservaService;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;

    @Autowired
    public ReservaController(ReservaService reservaService, VehiculoService vehiculoService, UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaEntity reserva) {
        try {
            ReservaEntity nuevaReserva = reservaService.crearReserva(
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getCosto(),
                reserva.getUsuario()
                    .getId(),
                reserva.getVehiculo()
                    .getId(),
                reserva.getSucursal()
                    .getId());
            return ResponseEntity.ok(nuevaReserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/obtener")
    public List<ReservaEntity> obtenerReservas() {
        return reservaService.obtenerReservas();
    }

    @GetMapping("/obtener/porUsuario")
    public ResponseEntity<?> obtenerReservasPorUsuario(@RequestParam Long id) {
        try {
            UsuarioEntity u = usuarioService.obtenerUsuarioPorId(id);
            List<ReservaEntity> reservas = reservaService.obtenerReservasDeUsuario(u);
            return ResponseEntity.ok()
                .body(reservas);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id,
                                              @RequestBody ReservaEntity.EstadoReserva estado) {
        try {
            ReservaEntity reserva = reservaService.actualizarEstado(id, estado);
            return ResponseEntity.ok(reserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/obtener/porVehiculo")
    public ResponseEntity<?> obtenerReservasPorVehiculo(@RequestParam Long id) {
        try {
            VehiculoEntity v = vehiculoService.obtenerVehiculoPorId(id);
            List<ReservaEntity> reservas = reservaService.obtenerReservasDeVehiculo(v);
            return ResponseEntity.ok()
                .body(reservas);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PutMapping("/extender")
    public ResponseEntity<?> extenderReserva(@RequestBody Map<String, Object> nuevaFechaJsonMap) {
        try {
            // En el cuerpo de la peticion va un json de dos componentes:
            //  reservaId, fechaFin (nueva fecha de termino)

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            reservaService.extenderReserva(
                Long.valueOf((Integer) nuevaFechaJsonMap.get("reservaId")),
                LocalDateTime.parse((String) nuevaFechaJsonMap.get("fechaFin"), formatoFecha));
            return ResponseEntity.ok()
                .body("Reserva extendida");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable ReservaEntity.EstadoReserva estado) {
        try {
            List<ReservaEntity> reservas = reservaService.obtenerPorEstado(estado);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}
