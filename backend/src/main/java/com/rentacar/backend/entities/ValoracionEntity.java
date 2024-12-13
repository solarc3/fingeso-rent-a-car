package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "valoraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(nullable = false)
    private String comentario;
    // una valoracion debe tener asignado un usuario y un vehiculo
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private UsuarioEntity usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehiculo_id")
    @JsonBackReference
    private VehiculoEntity vehiculo;
}
