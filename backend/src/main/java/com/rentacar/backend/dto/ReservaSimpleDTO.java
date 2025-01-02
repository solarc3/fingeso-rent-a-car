package com.rentacar.backend.dto;

import com.rentacar.backend.entities.ReservaEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReservaSimpleDTO {
    private Long id;
    // Fechas
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    // Importante para generación de reportes
    private BigDecimal costo;
    // Maybe
    private ReservaEntity.EstadoReserva estado;
}
