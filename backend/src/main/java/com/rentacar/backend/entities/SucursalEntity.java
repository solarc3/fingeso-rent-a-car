package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String direccion;

    private String telefono;

    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.EAGER)
    @JsonManagedReference(value = "sucursal-usuario")
    private List<UsuarioEntity> empleados;

    @OneToMany(mappedBy = "sucursal")
    @JsonManagedReference(value = "sucursal-vehiculo")
    private List<VehiculoEntity> vehiculos;

    @OneToMany(mappedBy = "sucursal")
    @JsonManagedReference(value = "sucursal-reserva")
    private List<ReservaEntity> reservas;
}
