package com.rentacar.backend.dto;
import com.rentacar.backend.entities.UsuarioEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class UsuarioPruebaDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String rol;
    private boolean estaEnListaNegra;
}
