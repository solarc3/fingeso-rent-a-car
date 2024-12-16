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

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"sucursal", "valoraciones", "reservas"})
    private List<UsuarioEntity> empleados;

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"sucursal", "valoraciones", "reservas"})
    private List<VehiculoEntity> vehiculos;

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"sucursal", "usuario", "vehiculo"})
    private List<ReservaEntity> reservas;
}
