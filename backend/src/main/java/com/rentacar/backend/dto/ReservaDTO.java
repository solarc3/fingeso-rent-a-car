package com.rentacar.backend.dto;
import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReservaDTO {
    // Fechas
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    // Importante para generación de reportes
    private BigDecimal costo;
    // Maybe
    private ReservaEntity.EstadoReserva estado;
    // Si o si
    private UsuarioDTO usuario;
    private VehiculoDTO vehiculo;
    private SucursalDTO sucursal;
    // Maybe, sucursalDevolucion
    //private SucursalDTO sucursalDevolucion
}
