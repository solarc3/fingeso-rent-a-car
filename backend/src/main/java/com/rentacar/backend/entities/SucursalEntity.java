package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sucursal")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class SucursalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    // Nombre de la sucursal
    private String nombre;

    // Direccion de la sucursal
    private String direccion;

    // Telefono de contacto de la sucursal
    private String telefono;

    // Correo de contacto de la sucursal
    private String email;

    // Lista empleados de la sucursal
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<UsuarioEntity> empleados;

    // Lista vehiculos que estan en la sucursal
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<VehiculoEntity> vehiculos;

    @OneToMany(mappedBy= "sucursal",cascade = CascadeType.ALL)
    private List<ReservaEntity> reservas;
}
