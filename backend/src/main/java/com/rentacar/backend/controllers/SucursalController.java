package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.services.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sucursal")
public class SucursalController {
    private final SucursalService sucursalService;

    @Autowired
    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearSucursal(@RequestBody SucursalEntity sucursal) {
        try {
            SucursalEntity nuevaSucursal = sucursalService.crearSucursal(
                    sucursal.getNombre(),
                    sucursal.getDireccion(),
                    sucursal.getTelefono(),
                    sucursal.getEmail()
            );
            return ResponseEntity.ok().body(nuevaSucursal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Map<String, Object>>> listarSucursales() {
        List<SucursalEntity> sucursales = sucursalService.obtenerSucursales();
        List<Map<String, Object>> sucursalesSimplificadas = sucursales.stream()
                .map(sucursal -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", sucursal.getId());
                    map.put("nombre", sucursal.getNombre());
                    map.put("direccion", sucursal.getDireccion());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(sucursalesSimplificadas);
    }

    @GetMapping("/obtenerPorId")
    public ResponseEntity<?> obtenerSucursalPorId(@RequestParam("id") Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("El ID de la sucursal es requerido");
            }
            SucursalEntity sucursal = sucursalService.obtenerSucursalPorId(id);
            return ResponseEntity.ok(sucursal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarSucursalPorId(@RequestParam Long id, @RequestBody SucursalEntity sucursal) {
        try {
            SucursalEntity sucursalActualizada = sucursalService.actualizarSucursal(
                    id,
                    sucursal.getNombre(),
                    sucursal.getDireccion(),
                    sucursal.getTelefono(),
                    sucursal.getEmail()
            );
            return ResponseEntity.ok(sucursalActualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarSucursal(@RequestParam Long id) {
        try {
            sucursalService.eliminarSucursalPorId(id);
            return ResponseEntity.ok().body("Sucursal eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/agregarEmpleado")
    public ResponseEntity<?> agregarEmpleadoPorIdEmpleado(@RequestParam Long IdEmpleado, @RequestParam Long IdSucursal) {
        try {
            sucursalService.agregarEmpleado(IdEmpleado, IdSucursal);
            return ResponseEntity.ok().body("Empleado agregado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/agregarVehiculo")
    public ResponseEntity<?> agregarVehiculoPorIdVehiculo(@RequestParam Long IdVehiculo, @RequestParam Long IdSucursal) {
        try {
            sucursalService.agregarVehiculo(IdVehiculo, IdSucursal);
            return ResponseEntity.ok().body("Vehiculo agregado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}