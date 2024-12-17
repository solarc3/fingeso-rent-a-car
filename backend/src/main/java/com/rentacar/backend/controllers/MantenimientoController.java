package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.*;
import com.rentacar.backend.services.MantenimientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mantenimiento")
public class MantenimientoController {
    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @PostMapping("/programar")
    public ResponseEntity<?> programarMantenimiento(@RequestBody Map<String, Object> datos) {
        try {
            if (!datos.containsKey("vehiculoId") ||
                !datos.containsKey("fechaProgramada") ||
                !datos.containsKey("tipoMantenimiento")) {
                return ResponseEntity.badRequest()
                    .body("Faltan campos requeridos");
            }

            Long vehiculoId = Long.parseLong(datos.get("vehiculoId")
                                                 .toString());
            LocalDateTime fechaProgramada = LocalDateTime.parse(
                datos.get("fechaProgramada")
                    .toString()
                                                               );
            String tipoMantenimiento = datos.get("tipoMantenimiento")
                .toString();
            String descripcion = datos.getOrDefault("descripcion", "")
                .toString();
            Double costoEstimado = datos.containsKey("costoEstimado") ?
                                   Double.parseDouble(datos.get("costoEstimado")
                                                          .toString()) : 0.0;
            Long tecnicoId = datos.containsKey("tecnicoId") ?
                             Long.parseLong(datos.get("tecnicoId")
                                                .toString()) : null;

            MantenimientoEntity mantenimiento = mantenimientoService.programarMantenimiento(
                vehiculoId,
                fechaProgramada,
                tipoMantenimiento,
                descripcion,
                costoEstimado,
                tecnicoId
                                                                                           );

            return ResponseEntity.ok(mantenimiento);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/mantenimientos/{vehiculoId}")
    public ResponseEntity<?> obtenerMantenimientos(@PathVariable Long vehiculoId) {
        try {
            List<MantenimientoEntity> mantenimientos =
                mantenimientoService.obtenerMantenimientosVehiculo(vehiculoId);
            return ResponseEntity.ok(mantenimientos);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
        @PathVariable Long id,
        @RequestBody Map<String, String> estadoMap
                                             ) {
        try {
            String nuevoEstado = estadoMap.get("estado");
            if (nuevoEstado == null) {
                return ResponseEntity.badRequest()
                    .body("El estado es requerido");
            }

            MantenimientoEntity mantenimiento =
                mantenimientoService.actualizarEstadoMantenimiento(id, nuevoEstado);
            return ResponseEntity.ok(mantenimiento);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/historial/{vehiculoId}")
    public ResponseEntity<?> obtenerHistorialVehiculo(@PathVariable Long vehiculoId) {
        try {
            List<HistorialVehiculoEntity> historial = mantenimientoService.obtenerHistorialVehiculo(vehiculoId);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}