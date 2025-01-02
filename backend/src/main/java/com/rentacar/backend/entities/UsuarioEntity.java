package com.rentacar.backend.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuario_rut", columnList = "rut"),
    @Index(name = "idx_usuario_rol", columnList = "rol")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity implements UserDetails {
    public enum RolUsuario {
        ARRENDATARIO,
        TRABAJADOR,
        ADMINISTRADOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rut;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private boolean estaEnListaNegra;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean softDelete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @Column(nullable = false)
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority( rol.name()));
    }

    @Override
    public String getUsername() {
        return rut;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !estaEnListaNegra;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !softDelete;
    }

}