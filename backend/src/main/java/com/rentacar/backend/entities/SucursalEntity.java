package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
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
    @JsonManagedReference(value = "sucursal-usuario")
    private List<UsuarioEntity> empleados;

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.EAGER)
    @JsonManagedReference(value = "sucursal-vehiculo")
    private List<VehiculoEntity> vehiculos;

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.EAGER)
    @JsonManagedReference(value = "sucursal-reserva")
    private List<ReservaEntity> reservas;
}
