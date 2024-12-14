package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.services.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//Crear
//Leer todos, algunos por id, eliminar

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
                    sucursal.getEmail());
            return ResponseEntity.ok().body(nuevaSucursal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/listar")
    public ResponseEntity<List<SucursalEntity>> listarSucursales(){
        return ResponseEntity.ok(sucursalService.obtenerSucursales());
    }

    @GetMapping("/obtenerPorId")
    public ResponseEntity<?> obtenerSucursalPorId(@RequestParam Long id) {
        try {
            return ResponseEntity.ok(sucursalService.obtenerSucursalPorId(id));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Params id
    //Body sucursal {nombre, direccion, telefono, email}
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarSucursalPorId(@RequestParam Long id, @RequestBody SucursalEntity sucursal){
        try{
            SucursalEntity sucursalActualizada = sucursalService.actualizarSucursal(
                    id,
                    sucursal.getNombre(),
                    sucursal.getDireccion(),
                    sucursal.getTelefono(),
                    sucursal.getEmail());
            return ResponseEntity.ok(sucursalActualizada);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarSucursal(@RequestParam Long id){
        try{
            sucursalService.eliminarSucursalPorId(id);
            return ResponseEntity.ok().body("Sucursal eliminada correctamente");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/agregarEmpleado")
    public ResponseEntity<?> agregarEmpleadoPorIdEmpleado(@RequestParam Long IdEmpleado, @RequestParam Long IdSucursal){
        try{
            sucursalService.agregarEmpleado(IdEmpleado, IdSucursal);
            return ResponseEntity.ok().body("Empleado agregado correctamente");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/agregarVehiculo")
    public ResponseEntity<?> agregarVehiculoPorIdEmpleado(@RequestParam Long IdVehiculo, @RequestParam Long IdSucursal){
        try{
            sucursalService.agregarVehiculo(IdVehiculo, IdSucursal);
            return ResponseEntity.ok().body("Vehiculo agregado correctamente");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
