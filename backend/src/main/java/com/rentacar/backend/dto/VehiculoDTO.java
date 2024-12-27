package com.rentacar.backend.dto;

import com.rentacar.backend.dto.SucursalDTO;
import com.rentacar.backend.entities.VehiculoEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class VehiculoDTO {
    private Long id;
    private String marca;
    private String modelo;
    private String acriss;
    private String patente;
    private BigDecimal precioArriendo;
    private Integer anio;
    private VehiculoEntity.EstadoVehiculo estado;
    private Long sucursalId;
}

