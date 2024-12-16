package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_vehiculo")
@Data
public class HistorialVehiculoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private VehiculoEntity vehiculo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String tipoEvento; // MANTENIMIENTO, REPARACION, CAMBIO_ESTADO, etc.

    private String descripcion;

    @Column(nullable = false)
    private String estadoAnterior;

    @Column(nullable = false)
    private String estadoNuevo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity registradoPor;
}