package com.rentacar.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    // Fecha - horarios de la reserva
    private Date fecha;

    // Costo de la reserva del vehiculo
    private Float costo;

    // añadir el ID usuario e ID vehiculo

    // tmb se deberia ver el tema del estado de la reserva

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="id_usuario",referencedColumnName = "id")
    private UsuarioEntity usuario;

    @OnetoOne(cascade = CascadeType.ALL)
    @JoinColumn(name="id_vehiculo",referencedColumnName = "id")
    private VehiculoEntity vehiculo;


}
