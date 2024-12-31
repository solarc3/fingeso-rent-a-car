package com.rentacar.backend.controllers;

import com.rentacar.backend.dto.UsuarioDTO;
import com.rentacar.backend.dto.UsuarioPruebaDTO;
import com.rentacar.backend.dto.UsuarioLoginDTO;
import com.rentacar.backend.dto.UsuarioRegistroDTO;
import com.rentacar.backend.entities.SucursalEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.services.SucursalService;
import com.rentacar.backend.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SucursalService sucursalService;
    private final ModelMapper modelMapper;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, SucursalService sucursalService,ModelMapper modelMapper) {
        this.usuarioService = usuarioService;
        this.sucursalService = sucursalService;
        this.modelMapper = modelMapper;
    }

    @PutMapping("/{id}/sucursal")
    public ResponseEntity<?> asignarSucursal(@PathVariable Long id, @RequestParam Long sucursalId) {
        try {
            UsuarioEntity usuario = usuarioService.asignarSucursal(id, sucursalId);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioRegistroDTO usuario) {
        try {
            String rut = usuario.getRut();
            String nombre = usuario.getNombre();
            String apellido = usuario.getApellido();
            String password = usuario.getPassword();
            UsuarioEntity.RolUsuario rol = usuario.getRol();

            if (rut == null || nombre == null || apellido == null || password == null) {
                return ResponseEntity.badRequest()
                        .body("Todos los campos son requeridos");
            }

            UsuarioEntity nuevoUsuario = usuarioService.crearUsuario(
                    rut,
                    nombre,
                    apellido,
                    password,
                    rol);

            if (usuario.getSucursalId() != null) {
                Long sucursalId = Long.valueOf(usuario.getSucursalId()
                        .toString());
                sucursalService.agregarEmpleado(nuevoUsuario.getId(), sucursalId);
            }

            return ResponseEntity.ok()
                    .body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }


    //IMPLEMENTAR DTO DESDE AQUI A ABAJO?
    @GetMapping("/trabajadores")
    public ResponseEntity<?> obtenerTrabajadores() {
        try {
            List<UsuarioEntity> trabajadores = usuarioService.obtenerTrabajadores();
            List<UsuarioPruebaDTO> trabajadoresDTO = trabajadores.stream()
                    .map(this::toUsuarioPruebaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(trabajadoresDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/administradores")
    public ResponseEntity<?> obtenerAdministradores() {
        try {
            List<UsuarioEntity> administradores = usuarioService.obtenerAdministradores();
            List<UsuarioPruebaDTO> administradoresDTO = administradores.stream()
                    .map(this::toUsuarioPruebaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(administradoresDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/arrendatarios")
    public ResponseEntity<?> obtenerArrendatarios() {
        try {
            List<UsuarioEntity> arrendatarios = usuarioService.obtenerArrendatarios();
            List<UsuarioPruebaDTO> arrendatariosDTO = arrendatarios.stream()
                    .map(this::toUsuarioPruebaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(arrendatariosDTO);
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
                    .body(new HashMap<String, String>() {{
                        put("mensaje", "Usuario eliminado correctamente");
                    }});
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new HashMap<String, String>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestParam Long id, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioEntity usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDTO credentials) {
        try {
            String rut = credentials.getRut();
            String password = credentials.getPassword();
            String rol = String.valueOf(credentials.getRol());

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
                //return ResponseEntity.ok(user);
                UsuarioPruebaDTO respuesta = new UsuarioPruebaDTO();
                respuesta.setId(user.getId());
                respuesta.setNombre(user.getNombre());
                respuesta.setApellido(user.getApellido());
                respuesta.setRut(user.getRut());
                respuesta.setRol(user.getRol().name());

                return ResponseEntity.ok(respuesta);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales invalidas");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validacion: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
    public UsuarioPruebaDTO toUsuarioPruebaDTO(UsuarioEntity usuarioEntity){
        return modelMapper.map(usuarioEntity, UsuarioPruebaDTO.class);
    }
}
