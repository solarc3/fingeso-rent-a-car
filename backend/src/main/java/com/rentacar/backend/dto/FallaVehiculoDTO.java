package com.rentacar.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FallaVehiculoDTO {
        private Long vehiculoId;
        private String tipo;
        private String severidad;
        private String descripcion;
        private Long reportadoPorId;
}
