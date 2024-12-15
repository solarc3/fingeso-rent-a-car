package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.services.SucursalService;
import com.rentacar.backend.services.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/vehiculo")
public class VehiculoController {
    private final VehiculoService vehiculoService;
    private final SucursalService sucursalService;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService, SucursalService sucursalService) {
        this.vehiculoService = vehiculoService;
        this.sucursalService = sucursalService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearVehiculo(@RequestBody VehiculoEntity vehiculo) {
        try {
            vehiculoService.crearVehiculo(
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAcriss(),
                vehiculo.getPatente(),
                vehiculo.getPrecioArriendo());
            return ResponseEntity.ok()
                .body("Vehiculo creado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/listing")
    public List<VehiculoEntity> obtenerVehiculos() {
        return vehiculoService.obtenerVehiculos();
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
}
