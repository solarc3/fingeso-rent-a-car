package com.rentacar.backend.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuario_rut", columnList = "rut"),
    @Index(name = "idx_usuario_rol", columnList = "rol")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {
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

}