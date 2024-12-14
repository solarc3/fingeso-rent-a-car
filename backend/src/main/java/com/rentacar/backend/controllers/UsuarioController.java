package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioEntity usuario) {
        try{
            usuarioService.crearUsuario(
                    usuario.getRut(),
                    usuario.getNombre(),
                    usuario.getApellido());
                    return ResponseEntity.ok().body("Usuario creado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarUsuario(@RequestParam Long id) {
        try{
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok().body("Usuario eliminado correctamente");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestBody UsuarioEntity usuario, Long id) {
        try{
            usuarioService.actualizarUsuario(id,usuario);
            return ResponseEntity.ok().body("Usuario actualizado correctamente");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
