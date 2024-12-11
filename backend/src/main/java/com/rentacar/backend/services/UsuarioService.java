package com.rentacar.backend.services;

import com.rentacar.backend.entities.*;
import com.rentacar.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    //Buscar usuario por rut
    public Optional<UsuarioEntity> getUsuarioByRut(String rut){
        return usuarioRepository.findByRut(rut);
    }

    //Buscar usuarios por estado de lista negra
    public List<UsuarioEntity> getUsuariosEnListaNegra(boolean estaEnListaNegra){
        return usuarioRepository.findByEstaEnListaNegra(estaEnListaNegra);
    }
    //Buscar usuarios por valoraciones
    public List<UsuarioEntity> getUsuariosPorReservas(ReservaEntity reservas){
        return usuarioRepository.findByReservas(reservas);
    }

    //Buscar usuarios por sucursal
    public List<UsuarioEntity> getUsuariosPorSucursal(SucursalEntity sucursal){
        return usuarioRepository.findBySucursal(sucursal);
    }

    //Crear nuevo usuario
    public UsuarioEntity crearUsuario(String rut, String nombre, String apellido){
        if (usuarioRepository.findByRut(rut).isPresent())
            throw new RuntimeException("Ya existe un usuario con RUT " + rut);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setRut(rut);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEstaEnListaNegra(false);
        return usuarioRepository.save(usuario);
    }

    //Actualizar usuario que ya existe, por id
    public UsuarioEntity actualizarUsuario(Long id, UsuarioEntity usuarioActualizado){
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            usuarioActualizado.setId(id);
            return usuarioRepository.save(usuarioActualizado);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    //Eliminar usuario por id
    public void eliminarUsuario(Long id){
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);
        if(usuarioExistente.isPresent()){
            usuarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }
}
