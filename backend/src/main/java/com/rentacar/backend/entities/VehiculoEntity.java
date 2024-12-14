package com.rentacar.backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;

	@Column(nullable = false)
	private String marca;

	@Column(nullable = false)
	private String modelo;

	// Código ACRISS asociado al vehículo
	@Column(nullable = false, length = 4)
	private String acriss;

	@Column(unique = true, nullable = false, length = 6)
	private String patente;

	// fab year
	private int anio;

	@Column(name = "precio_arriendo", nullable = false)
	private BigDecimal precioArriendo;

	@ManyToOne
	@JoinColumn(name = "sucursal_id")
	@JsonBackReference(value = "sucursal-vehiculo")
	private SucursalEntity sucursal;

	// Disponibilidad
	private boolean disponible;

	// Estado del vehiculo
	private String estado;//DISPONIBLE, NO_DISPONIBLE, EN_MANTENCION, EN_REPARACION

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference(value = "vehiculo-valoracion")
	private List<ValoracionEntity> valoraciones;
}