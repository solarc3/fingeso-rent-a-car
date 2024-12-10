package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "vehiculos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(nullable = false)
    private String marca;
    @Column(nullable = false)
    private String modelo;

    @Column(unique = true, nullable = false, length = 6)
    private String patente;

    // fab year
    private int anio;

    @Column(name = "precio_arriendo", nullable = false)
    private BigDecimal precioArriendo;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private SucursalEntity sucursal;

    // Disponibilidad
    private boolean disponible;

    // Estado del vehiculo
    private String estado;//DISPONIBLE, NO_DISPONIBLE, EN_MANTENCION, EN_REPARACION

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    private List<ValoracionEntity> valoraciones;
}
