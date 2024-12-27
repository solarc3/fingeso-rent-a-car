package com.rentacar.backend.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioRespuestaDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String rol;
}
