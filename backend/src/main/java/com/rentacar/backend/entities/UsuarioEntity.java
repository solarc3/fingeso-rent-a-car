package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    // RUT asociado al usuario.
    private String usuarioRUT;

    // Nombre en pantalla del usuario
    private String usuarioName;

    // Apellido en pantalla del usuario
    private String usuarioApellido;

    // O es Cliente, Trabajador o Administrador.
    private Integer usuarioAccountType;

    // Aweonao
    private boolean usuarioIsBlacklisted;

    //la relacion con lista negra debería ir en lista negra, el usuario
    //no necesita saber sobre listanegra.
}