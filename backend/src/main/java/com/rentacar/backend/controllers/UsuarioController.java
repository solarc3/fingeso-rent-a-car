package com.rentacar.backend.controllers;

import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.services.SucursalService;
import com.rentacar.backend.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SucursalService sucursalService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, SucursalService sucursalService) {
        this.usuarioService = usuarioService;
        this.sucursalService = sucursalService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> usuario) {
        try {
            String rut = (String) usuario.get("rut");
            String nombre = (String) usuario.get("nombre");
            String apellido = (String) usuario.get("apellido");
            String password = (String) usuario.get("password");
            UsuarioEntity.RolUsuario rol = UsuarioEntity.RolUsuario.valueOf((String) usuario.get("rol"));

            SucursalEntity sucursal = null;
            if (usuario.get("sucursalId") != null) {
                Long sucursalId = Long.valueOf(usuario.get("sucursalId")
                                                   .toString());
                sucursal = sucursalService.obtenerSucursalPorId(sucursalId);
            }
            
            if (rut == null || nombre == null || apellido == null || password == null) {
                return ResponseEntity.badRequest()
                    .body("Todos los campos son requeridos");
            }

            UsuarioEntity nuevoUsuario = usuarioService.crearUsuario(
                rut,
                nombre,
                apellido,
                password,
                rol,
                sucursal
                                                                    );

            return ResponseEntity.ok()
                .body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/trabajadores")
    public ResponseEntity<?> obtenerTrabajadores() {
        try {
            return ResponseEntity.ok(usuarioService.obtenerTrabajadores());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/administradores")
    public ResponseEntity<?> obtenerAdministradores() {
        try {
            return ResponseEntity.ok(usuarioService.obtenerAdministradores());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/arrendatarios")
    public ResponseEntity<?> obtenerArrendatarios() {
        try {
            return ResponseEntity.ok(usuarioService.obtenerArrendatarios());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarUsuario(@RequestParam Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok()
                .body("Usuario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestBody UsuarioEntity usuario, Long id) {
        try {
            usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok()
                .body("Usuario actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String rut = credentials.get("rut");
            String password = credentials.get("password");
            String rol = credentials.get("rol");

            if (rut == null || password == null || rol == null) {
                return ResponseEntity.badRequest()
                    .body("RUT, contraseña y rol son requeridos");
            }

            Optional<UsuarioEntity> usuario = usuarioService.validarCredenciales(rut, password, rol);

            if (usuario.isPresent()) {
                UsuarioEntity user = usuario.get();

                // check lista negra
                if (user.isEstaEnListaNegra()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Usuario en lista negra");
                }
                // si llegamos aqui todo ok
                return ResponseEntity.ok(user);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales invalidas");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}
