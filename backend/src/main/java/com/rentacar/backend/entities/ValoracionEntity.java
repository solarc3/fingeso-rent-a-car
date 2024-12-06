package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "valoracion")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ValoracionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    // Valoracion entre 1 y 5 puntos
    private Integer puntuacion;

    // Texto de la valoración/reseña como tal
    private String comentario;

    // ID del usuario que publica la valoración
    private Integer idUsuario;

    // ID del vehículo arrendado por el usuario
    private Integer idVehiculo;
}
