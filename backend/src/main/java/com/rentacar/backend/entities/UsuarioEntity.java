package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rut;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private boolean estaEnListaNegra;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    @JsonManagedReference(value = "usuario-valoracion")
    private List<ValoracionEntity> valoraciones;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    @JsonManagedReference(value = "usuario-reserva")
    private List<ReservaEntity> reservas;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    @JsonBackReference(value = "sucursal-usuario")
    private SucursalEntity sucursal;  // cuando es un empleado, se quiere saber su sucursal
}
