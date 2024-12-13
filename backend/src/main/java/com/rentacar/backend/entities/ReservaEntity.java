package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private BigDecimal costo;

    @Column(nullable = false)
    private Integer estado; //PENDIENTE, CONFIRMADA, EN_PROGRESO, COMPLETADA, CANCELADA

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    @JsonBackReference
    private VehiculoEntity vehiculo;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    @JsonBackReference
    private SucursalEntity sucursal;

}
