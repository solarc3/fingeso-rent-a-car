package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    public enum EstadoVehiculo {
        DISPONIBLE,
        EN_MANTENCION,
        EN_REPARACION,
        ARRENDADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false, length = 4)
    private String acriss;

    @Column(unique = true, nullable = false, length = 6)
    private String patente;

    private int anio;

    @Column(name = "precio_arriendo", nullable = false)
    private BigDecimal precioArriendo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sucursal_id")
    @JsonIgnoreProperties({"vehiculos", "empleados", "reservas"})
    private SucursalEntity sucursal;

    @OneToMany(mappedBy = "vehiculo", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"vehiculo", "usuario"})
    private List<ValoracionEntity> valoraciones;
    

    private boolean disponible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;
}