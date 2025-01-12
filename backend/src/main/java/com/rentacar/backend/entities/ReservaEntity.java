package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {
    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private BigDecimal costo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private VehiculoEntity vehiculo;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private SucursalEntity sucursal;

    @ManyToOne
    @JoinColumn(name = "sucursal_devolucion_id")  // New field for return location
    private SucursalEntity sucursalDevolucion;
}
