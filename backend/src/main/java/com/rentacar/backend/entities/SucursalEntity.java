package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sucursales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    private String telefono;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "sucursal")
    private List<UsuarioEntity> empleados;

    @OneToMany(mappedBy = "sucursal")
    private List<VehiculoEntity> vehiculos;

    @OneToMany(mappedBy = "sucursal")
    private List<ReservaEntity> reservas;
}
