package com.rentacar.backend.dto;

import com.rentacar.backend.entities.UsuarioEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioLoginDTO {
    private String rut;
    private String password;
    private UsuarioEntity.RolUsuario rol;
}
