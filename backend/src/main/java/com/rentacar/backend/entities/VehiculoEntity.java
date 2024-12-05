package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class VehiculoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable =false)
    private Long id;

    //Marca del vehiculo
    private String marca;

    //Modelo del vehiculo
    private String modelo;

    //Patente unica del vehiculo
    @Column(unique = true, nullable = false)
    private String patente;

    // Año de fabricacion del vehiulo
    private int anio;

    // Tipo de combustible
    private String tipoCombustible;

    // Capacidad de pasajeros
    private int capacidadPasajeros;

    // Precio de arriendo diario
    private double precioArriendoDiario;

    // Sucursal asociada
    private String sucursal;

    // Disponibilidad del vehiculo
    private boolean disponible;

    // Estado del vehiculo
    private String estado;

    // URL de una imagen del vehiculo
    private String urlImagen;
}
