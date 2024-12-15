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

    public List<UsuarioEntity> getUsuariosPorValoracion(ValoracionEntity valoracion) {
        return usuarioRepository.findByValoracionesContaining(valoracion);
    }

    public List<UsuarioEntity> getUsuariosPorValoraciones(List<ValoracionEntity> valoraciones) {
        return usuarioRepository.findByValoracionesIn(valoraciones);
    }

    public List<UsuarioEntity> getUsuariosPorReserva(ReservaEntity reserva) {
        return usuarioRepository.findByReservasContaining(reserva);
    }

    public List<UsuarioEntity> getUsuariosPorReservas(List<ReservaEntity> reservas) {
        return usuarioRepository.findByReservasIn(reservas);
    }

    //Buscar usuario por rut
    public Optional<UsuarioEntity> getUsuarioByRut(String rut) {
        return usuarioRepository.findByRut(rut);
    }

    //Buscar usuarios por estado de lista negra
    public List<UsuarioEntity> getUsuariosEnListaNegra(boolean estaEnListaNegra) {
        return usuarioRepository.findByEstaEnListaNegra(estaEnListaNegra);
    }

    //Buscar usuarios por sucursal
    public List<UsuarioEntity> getUsuariosPorSucursal(SucursalEntity sucursal) {
        return usuarioRepository.findBySucursal(sucursal);
    }

    //Crear nuevo usuario
    public UsuarioEntity crearUsuario(String rut, String nombre, String apellido,
                                      String password, UsuarioEntity.RolUsuario rol,
                                      SucursalEntity sucursal) {
        // Verificar si ya existe un usuario con ese RUT
        if (usuarioRepository.findByRut(rut)
            .isPresent()) {
            throw new RuntimeException("Ya existe un usuario con RUT " + rut);
        }

        // Validaciones de contraseña
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

    //Actualizar usuario que ya existe, por id
    public UsuarioEntity actualizarUsuario(Long id, UsuarioEntity usuarioActualizado) {
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            usuarioActualizado.setId(id);
            return usuarioRepository.save(usuarioActualizado);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    //Eliminar usuario por id
    public void eliminarUsuario(Long id) {
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            usuarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    public List<UsuarioEntity> obtenerUsuariosPorRol(UsuarioEntity.RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
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
    

    public Optional<UsuarioEntity> validarCredenciales(String rut, String password) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findByRut(rut);

        if (usuario.isPresent() && usuario.get()
            .getPassword()
            .equals(password)) {
            return usuario;
        }

        return Optional.empty();
    }
}
