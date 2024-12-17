package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "fallas_vehiculo")
@Data
public class FallaVehiculoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private VehiculoEntity vehiculo;

    @Column(nullable = false)
    private String tipo; // MECANICA, ELECTRICA, CARROCERIA, etc.

    @Column(nullable = false)
    private String severidad; // BAJA, MEDIA, ALTA, CRITICA

    @Column
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaReporte;

    @ManyToOne
    @JoinColumn(name = "reportado_por_id")
    private UsuarioEntity reportadoPor;

    // Campo adicional para simplificar la API
    @Transient
    private Long reportadoPorId;
}