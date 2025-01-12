package com.rentacar.backend.services;

import com.rentacar.backend.dto.UsuarioDTO;
import com.rentacar.backend.entities.*;
import com.rentacar.backend.repositories.ReservaRepository;
import com.rentacar.backend.repositories.SucursalRepository;
import com.rentacar.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, ReservaRepository reservaRepository, SucursalRepository sucursalRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.sucursalRepository = sucursalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioEntity obtenerUsuarioPorId(Long id) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) throw new NoSuchElementException("Usuario no encontrado");
        return usuario.get();
    }


    // Actualizar usuario que ya existe, por id
    public UsuarioEntity actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Optional<UsuarioEntity> usuarioExistente = usuarioRepository.findById(id);

        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }

        UsuarioEntity usuario = usuarioExistente.get();

        if (usuarioDTO.getNombre() != null) {
            usuario.setNombre(usuarioDTO.getNombre());
        }
        if (usuarioDTO.getApellido() != null) {
            usuario.setApellido(usuarioDTO.getApellido());
        }
        if (usuarioDTO.getPassword() != null) {
            usuario.setPassword(usuarioDTO.getPassword());
        }
        if (usuarioDTO.getRol() != null) {
            usuario.setRol(usuarioDTO.getRol());
        }

        // Handle sucursal assignment
        if (usuarioDTO.getSucursalId() != null) {
            SucursalEntity sucursal = sucursalRepository.findById(usuarioDTO.getSucursalId())
                    .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + usuarioDTO.getSucursalId()));

            //usuario.setSucursal(sucursal);
            sucursal.getEmpleados().add(usuario);
            sucursalRepository.save(sucursal);
        }

        usuario.setEstaEnListaNegra(usuarioDTO.isEstaEnListaNegra());

        return usuarioRepository.save(usuario);
    }

    // Eliminar usuario por id
    public void eliminarUsuario(Long id) {
        Optional<UsuarioEntity> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }

        UsuarioEntity usuario = usuarioOptional.get();


        // Check if user has active reservations
        List<ReservaEntity> reservasActivas = reservaRepository.findByUsuario(usuario)
                .stream()
                .filter(r -> r.getEstado() == ReservaEntity.EstadoReserva.EN_PROGRESO ||
                        r.getEstado() == ReservaEntity.EstadoReserva.CONFIRMADA)
                .toList();

        if (!reservasActivas.isEmpty()) {
            throw new RuntimeException("No se puede eliminar un usuario con reservas activas");
        }

        try {
            usuario.setSoftDelete(true);
            usuarioRepository.save(usuario);

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el usuario: " + e.getMessage());
        }
    }
    //Asignar sucursal a un usuario al momento de crear un usuario.
    public UsuarioEntity asignarSucursal(Long usuarioId, Long sucursalId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        SucursalEntity sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con ID: " + sucursalId));

        //usuario.setSucursal(sucursal);
        return usuarioRepository.save(usuario);
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

    public List<UsuarioEntity> obtenerUsuariosActivos() {
        return usuarioRepository.findBysoftDelete(false);
    }

    @Override
    public UserDetails loadUserByUsername(String rut)
        throws UsernameNotFoundException {
        return usuarioRepository.findByRut(rut)
            .orElseThrow(() ->
                             new UsernameNotFoundException("Usuario no encontrado"));
    }

    public UsuarioEntity crearUsuario(String rut, String nombre,
                                      String apellido, String password, UsuarioEntity.RolUsuario rol) {
        if (usuarioRepository.findByRut(rut).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setRut(rut);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

    public Optional<UsuarioEntity> validarCredenciales(
        String rut, String password, String rol) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findByRut(rut);

        if (usuario.isPresent() &&
            passwordEncoder.matches(password, usuario.get().getPassword()) &&
            usuario.get().getRol().name().equals(rol) &&
            !usuario.get().isSoftDelete()) {
            return usuario;
        }

        return Optional.empty();
    }
}




