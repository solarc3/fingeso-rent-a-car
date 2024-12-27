package com.rentacar.backend.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "sucursales")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor

public class SucursalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String direccion;

    private String telefono;

    @Column(nullable = false)
    private String email;

    @OneToMany
    @JoinColumn(name = "sucursal_id")
    private List<UsuarioEntity> empleados;

    @OneToMany
    @JoinColumn(name = "sucursal_id")
    private List<VehiculoEntity> vehiculos;

    @OneToMany
    @JoinColumn(name = "sucursal_id")
    private List<ReservaEntity> reservas;
}
