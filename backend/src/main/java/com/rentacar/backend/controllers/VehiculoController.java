package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.services.SucursalService;
import com.rentacar.backend.services.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
            return ResponseEntity.ok().body("Vehiculo creado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
                    .filter(v -> Objects.equals(v.getEstado(), "DISPONIBLE"))
                    .toList();

            return ResponseEntity.ok().body(vSucursalDisp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
