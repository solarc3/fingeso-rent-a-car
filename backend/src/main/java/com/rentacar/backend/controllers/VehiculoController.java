package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.FallaVehiculoEntity;
import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.repositories.VehiculoRepository;
import com.rentacar.backend.services.SucursalService;
import com.rentacar.backend.services.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/vehiculo")
public class VehiculoController {
    private final VehiculoService vehiculoService;
    private final SucursalService sucursalService;
    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService, SucursalService sucursalService, VehiculoRepository vehiculoRepository) {
        this.vehiculoService = vehiculoService;
        this.sucursalService = sucursalService;
        this.vehiculoRepository = vehiculoRepository;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearVehiculo(@RequestBody Map<String, Object> vehiculoData) {
        try {
            Long sucursalId;
            if (vehiculoData.get("sucursal") instanceof Number) {
                sucursalId = ((Number) vehiculoData.get("sucursal")).longValue();
            } else if (vehiculoData.get("sucursal") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sucursalMap = (Map<String, Object>) vehiculoData.get("sucursal");
                sucursalId = ((Number) sucursalMap.get("id")).longValue();
            } else {
                throw new IllegalArgumentException("Formato de sucursal inválido");
            }

            // Crear vehículo
            VehiculoEntity nuevoVehiculo = vehiculoService.crearVehiculo(
                (String) vehiculoData.get("marca"),
                (String) vehiculoData.get("modelo"),
                (String) vehiculoData.get("acriss"),
                (String) vehiculoData.get("patente"),
                new BigDecimal(vehiculoData.get("precioArriendo")
                                   .toString()),
                (Integer) vehiculoData.get("anio"),
                VehiculoEntity.EstadoVehiculo.valueOf((String) vehiculoData.get("estado")),
                sucursalId
                                                                        );

            return ResponseEntity.ok(nuevoVehiculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el vehículo: " + e.getMessage());
        }
    }


    @GetMapping("/listing")
    public ResponseEntity<List<VehiculoEntity>> obtenerVehiculos() {
        List<VehiculoEntity> vehiculos = vehiculoRepository.findAllWithSucursales();

        // Forzar la inicialización de la relación con sucursal
        vehiculos.forEach(v -> {
            if (v.getSucursal() != null) {
                v.getSucursal()
                    .getId();
                v.getSucursal()
                    .getNombre();
            }
        });

        return ResponseEntity.ok(vehiculos);
    }


    @GetMapping("/listing/enSucursal")
    public ResponseEntity<List<VehiculoEntity>> obtenerVehiculosDisponiblesEnSucursal(@RequestParam Long id) {
        try {
            SucursalEntity sucursal = sucursalService.obtenerSucursalPorId(id);
            List<VehiculoEntity> vSucursal = vehiculoService.obtenerVehiculosPorSucursal(sucursal);

            // Filtrar vehiculos disponibles
            List<VehiculoEntity> vSucursalDisp = vSucursal.stream()
                .filter(v -> v.getEstado() == VehiculoEntity.EstadoVehiculo.DISPONIBLE)
                .toList();

            return ResponseEntity.ok()
                .body(vSucursalDisp);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarVehiculo(@RequestParam Long id) {
        try {
            vehiculoService.eliminarVehiculoPorId(id);
            return ResponseEntity.ok()
                .body("Vehiculo eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // actualizar precio y estado, los dos unicos atributos variables en el tiempo (?
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarVehiculo(@RequestBody VehiculoEntity vehiculo) {
        try {
            vehiculoService.actualizarPrecioArriendoVehiculoPorId(vehiculo.getId(),
                                                                  vehiculo.getPrecioArriendo());
            VehiculoEntity v = vehiculoService.actualizarEstadoVehiculoPorId(vehiculo.getId(),
                                                                             String.valueOf(vehiculo.getEstado()));
            return ResponseEntity.ok()
                .body(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(vehiculo);
        }
    }

    @PostMapping("/{id}/falla")
    public ResponseEntity<?> reportarFalla(@PathVariable Long id, @RequestBody FallaVehiculoEntity falla) {
        try {
            VehiculoEntity vehiculo = vehiculoService.reportarFalla(id, falla);
            return ResponseEntity.ok(vehiculo);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PostMapping("/falla")
    public ResponseEntity<?> reportarFalla(@RequestBody Map<String, Object> fallaData) {
        try {
            if (!fallaData.containsKey("vehiculoId") ||
                !fallaData.containsKey("tipo") ||
                !fallaData.containsKey("severidad") ||
                !fallaData.containsKey("descripcion") ||
                !fallaData.containsKey("reportadoPorId")) {
                return ResponseEntity.badRequest()
                    .body("Faltan campos requeridos");
            }

            Long vehiculoId = Long.parseLong(fallaData.get("vehiculoId")
                                                 .toString());

            FallaVehiculoEntity falla = new FallaVehiculoEntity();
            falla.setTipo(fallaData.get("tipo")
                              .toString());
            falla.setSeveridad(fallaData.get("severidad")
                                   .toString());
            falla.setDescripcion(fallaData.get("descripcion")
                                     .toString());
            falla.setReportadoPorId(Long.parseLong(fallaData.get("reportadoPorId")
                                                       .toString()));

            VehiculoEntity vehiculo = vehiculoService.reportarFalla(vehiculoId, falla);
            return ResponseEntity.ok(vehiculo);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error: " + e.getMessage());
        }
    }
}

