package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mantenimientos")
@Data
public class MantenimientoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private VehiculoEntity vehiculo;

    @Column(nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(nullable = true)
    private LocalDateTime fechaRealizada;

    @Column(nullable = false)
    private String tipoMantenimiento; // PREVENTIVO, CORRECTIVO

    private String descripcion;

    @Column(nullable = false)
    private String estado; // PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO

    @Column(nullable = false)
    private Double costo;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private UsuarioEntity tecnico;
}
