package com.rentacar.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioLoginDTO {
    private String rut;
    private String password;
    private String rol;
}
