package com.rentacar.backend.controllers;

import com.rentacar.backend.services.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/vehicle-brands")
    public ResponseEntity<List<String>> getVehicleBrands() {
        return ResponseEntity.ok(vehiculoService.obtenerMarcasUnicas());
    }

    @GetMapping("/vehicle-types")
    public ResponseEntity<Map<String, String>> getVehicleTypes() {
        return ResponseEntity.ok(vehiculoService.obtenerTiposVehiculo());
    }

    @GetMapping("/transmission-types")
    public ResponseEntity<Map<String, String>> getTransmissionTypes() {
        return ResponseEntity.ok(vehiculoService.obtenerTiposTransmision());
    }
}