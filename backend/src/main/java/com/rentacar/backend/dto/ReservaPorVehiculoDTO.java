package com.rentacar.backend.dto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReservaPorVehiculoDTO {
    private String vehiculo; // marca + modelo
    private int cantidadReservas;
    private BigDecimal ingresoTotal;
}
