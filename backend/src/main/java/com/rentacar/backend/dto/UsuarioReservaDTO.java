package com.rentacar.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioReservaDTO{
    private Long id;
    private String nombre;
    private String apellido;
}