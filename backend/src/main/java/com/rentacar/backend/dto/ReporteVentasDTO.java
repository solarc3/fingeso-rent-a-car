package com.rentacar.backend.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ReporteVentasDTO {
    private int totalReservas;
    private BigDecimal ingresoTotal;
    private Map<String, Integer> reservasPorEstado;
    private List<ReservaPorVehiculoDTO> reservasPorVehiculo;
    private Map<String, BigDecimal> ingresosPorSucursal;
    private Map<String, Integer> vehiculosMasReservados;
}