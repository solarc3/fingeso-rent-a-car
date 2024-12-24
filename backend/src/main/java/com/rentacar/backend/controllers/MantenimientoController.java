package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.*;
import com.rentacar.backend.services.MantenimientoService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mantenimiento")
public class MantenimientoController {
    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @PostMapping("/programar")
    public ResponseEntity<MantenimientoEntity> programarMantenimiento(@RequestBody ProgramarMantenimientoDTO request) {
        try {
            MantenimientoEntity mantenimiento = mantenimientoService.programarMantenimiento(
                request.getVehiculoId(),
                request.getFechaProgramada(),
                request.getTipoMantenimiento(),
                request.getDescripcion(),
                request.getCostoEstimado(),
                request.getTecnicoId()
                                                                                           );
            return ResponseEntity.ok(mantenimiento);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    @GetMapping("/mantenimientos/{vehiculoId}")
    public ResponseEntity<List<MantenimientoEntity>> obtenerMantenimientos(@PathVariable Long vehiculoId) {
        try {
            List<MantenimientoEntity> mantenimientos =
                mantenimientoService.obtenerMantenimientosVehiculo(vehiculoId);
            return ResponseEntity.ok(mantenimientos);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<MantenimientoEntity> actualizarEstado(
        @PathVariable Long id,
        @RequestBody ActualizarEstadoDTO request) {
        try {
            MantenimientoEntity mantenimiento =
                mantenimientoService.actualizarEstadoMantenimiento(id, request.getEstado());
            return ResponseEntity.ok(mantenimiento);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    @GetMapping("/historial/{vehiculoId}")
    public ResponseEntity<List<HistorialVehiculoEntity>> obtenerHistorialVehiculo(@PathVariable Long vehiculoId) {
        try {
            List<HistorialVehiculoEntity> historial =
                mantenimientoService.obtenerHistorialVehiculo(vehiculoId);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }
}

@Data
@NoArgsConstructor
class ProgramarMantenimientoDTO {
    private Long vehiculoId;
    private LocalDateTime fechaProgramada;
    private String tipoMantenimiento;
    private String descripcion;
    private Double costoEstimado;
    private Long tecnicoId;
}

@Data
@NoArgsConstructor
class ActualizarEstadoDTO {
    private String estado;
}