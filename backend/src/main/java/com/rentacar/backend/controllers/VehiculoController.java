package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.FallaVehiculoEntity;
import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.repositories.VehiculoRepository;
import com.rentacar.backend.services.SucursalService;
import com.rentacar.backend.services.VehiculoService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehiculo")
public class VehiculoController {
    private final VehiculoService vehiculoService;
    private final SucursalService sucursalService;
    private final ModelMapper modelMapper;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService, SucursalService sucursalService, ModelMapper modelMapper) {
        this.vehiculoService = vehiculoService;
        this.sucursalService = sucursalService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/crear")
    public ResponseEntity<VehiculoDTO> crearVehiculo(@RequestBody VehiculoDTO dto) {
        try {
            VehiculoEntity vehiculo = vehiculoService.crearVehiculo(
                dto.getMarca(),
                dto.getModelo(),
                dto.getAcriss(),
                dto.getPatente(),
                dto.getPrecioArriendo(),
                dto.getAnio(),
                dto.getEstado(),
                dto.getSucursal()
                    .getId()
                                                                   );

            return ResponseEntity.ok(modelMapper.map(vehiculo, VehiculoDTO.class));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    @GetMapping("/listing")
    public ResponseEntity<List<VehiculoDTO>> listarVehiculos() {
        List<VehiculoEntity> vehiculos = vehiculoService.obtenerTodos();
        List<VehiculoDTO> vehiculosDTO = vehiculos.stream()
            .map(vehiculo -> modelMapper.map(vehiculo, VehiculoDTO.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(vehiculosDTO);
    }

    @GetMapping("/listing/enSucursal")
    public ResponseEntity<List<VehiculoDTO>> obtenerVehiculosDisponiblesEnSucursal(@RequestParam Long id) {
        try {
            SucursalEntity sucursal = sucursalService.obtenerSucursalPorId(id);
            List<VehiculoEntity> vehiculos = vehiculoService.obtenerVehiculosPorSucursal(sucursal)
                .stream()
                .filter(v -> v.getEstado() == VehiculoEntity.EstadoVehiculo.DISPONIBLE)
                .toList();

            List<VehiculoDTO> vehiculosDTO = vehiculos.stream()
                .map(vehiculo -> modelMapper.map(vehiculo, VehiculoDTO.class))
                .collect(Collectors.toList());

            return ResponseEntity.ok(vehiculosDTO);
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
                .body("Vehículo eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<VehiculoDTO> actualizarVehiculo(@RequestBody VehiculoDTO dto) {
        try {
            VehiculoEntity vehiculo = vehiculoService.obtenerVehiculoPorId(dto.getId());
            vehiculo.setPrecioArriendo(dto.getPrecioArriendo());
            vehiculo.setEstado(dto.getEstado());

            VehiculoEntity vehiculoActualizado = vehiculoService.actualizarVehiculo(vehiculo);
            return ResponseEntity.ok(modelMapper.map(vehiculoActualizado, VehiculoDTO.class));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }

    @PostMapping("/{id}/falla")
    public ResponseEntity<VehiculoDTO> reportarFalla(@PathVariable Long id, @RequestBody FallaVehiculoDTO dto) {
        try {
            FallaVehiculoEntity falla = modelMapper.map(dto, FallaVehiculoEntity.class);
            VehiculoEntity vehiculo = vehiculoService.reportarFalla(id, falla);
            return ResponseEntity.ok(modelMapper.map(vehiculo, VehiculoDTO.class));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }
}

@Data
@NoArgsConstructor
class VehiculoDTO {
    private Long id;
    private String marca;
    private String modelo;
    private String acriss;
    private String patente;
    private BigDecimal precioArriendo;
    private Integer anio;
    private VehiculoEntity.EstadoVehiculo estado;
    private SucursalDTO sucursal;
}

@Data
@NoArgsConstructor
class SucursalDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
}

@Data
@NoArgsConstructor
class FallaVehiculoDTO {
    private Long vehiculoId;
    private String tipo;
    private String severidad;
    private String descripcion;
    private Long reportadoPorId;
}

