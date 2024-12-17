package com.rentacar.backend.services;

import com.rentacar.backend.entities.*;
import com.rentacar.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioEntity obtenerUsuarioPorId(Long id) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) throw new NoSuchElementException("Usuario no encontrado");
        return usuario.get();
    }

    //Crear nuevo usuario
    public UsuarioEntity crearUsuario(String rut, String nombre, String apellido,
                                      String password, UsuarioEntity.RolUsuario rol,
                                      SucursalEntity sucursal) {
        if (usuarioRepository.findByRut(rut)
            .isPresent()) {
            throw new RuntimeException("Ya existe un usuario con RUT " + rut);
        }

        if (password == null || password.trim()
            .isEmpty()) {
            throw new RuntimeException("La contraseña no puede estar vacía");
        }
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setRut(rut);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setPassword(password);
        usuario.setEstaEnListaNegra(false);
        usuario.setRol(rol);

        // Asignar sucursal si es necesario
        if (rol == UsuarioEntity.RolUsuario.TRABAJADOR ||
            rol == UsuarioEntity.RolUsuario.ADMINISTRADOR) {
            if (sucursal == null) {
                throw new RuntimeException("Los trabajadores y administradores deben tener una sucursal asignada");
            }
            usuario.setSucursal(sucursal);
        }

        return usuarioRepository.save(usuario);
    }

    // Actualizar usuario que ya existe, por id
    public UsuarioEntity actualizarUsuario(Long id, UsuarioEntity usuarioActualizado) {
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            usuarioActualizado.setId(id);
            return usuarioRepository.save(usuarioActualizado);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    // Eliminar usuario por id
    public void eliminarUsuario(Long id) {
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            usuarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }
    
    public List<UsuarioEntity> obtenerTrabajadores() {
        return usuarioRepository.findByRol(UsuarioEntity.RolUsuario.TRABAJADOR);
    }

    public List<UsuarioEntity> obtenerAdministradores() {
        return usuarioRepository.findByRol(UsuarioEntity.RolUsuario.ADMINISTRADOR);
    }

    public List<UsuarioEntity> obtenerArrendatarios() {
        return usuarioRepository.findByRol(UsuarioEntity.RolUsuario.ARRENDATARIO);
    }


    public Optional<UsuarioEntity> validarCredenciales(String rut, String password, String rol) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findByRut(rut);
        //pasar el rol de string a enum
        UsuarioEntity.RolUsuario rolUsuario = UsuarioEntity.RolUsuario.valueOf(rol);
        if (usuario.isPresent() && usuario.get()
            .getPassword()
            .equals(password) && usuario.get()
                                     .getRol() == rolUsuario) {
            return usuario;
        }

        return Optional.empty();
    }
}
