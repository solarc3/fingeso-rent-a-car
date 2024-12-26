package com.rentacar.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String password;
    private boolean estaEnListaNegra;
    private String rol;
    private Long sucursalId;
}


