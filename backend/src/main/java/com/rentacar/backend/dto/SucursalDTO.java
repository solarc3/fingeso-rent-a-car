package com.rentacar.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SucursalDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
}
