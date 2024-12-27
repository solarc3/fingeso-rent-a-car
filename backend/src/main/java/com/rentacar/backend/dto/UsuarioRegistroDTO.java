package com.rentacar.backend.dto;

import com.rentacar.backend.entities.UsuarioEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioRegistroDTO {
    private String rut;
    private String nombre;
    private String apellido;
    private String password;
    private UsuarioEntity.RolUsuario rol;
    private Long sucursalId;
}
