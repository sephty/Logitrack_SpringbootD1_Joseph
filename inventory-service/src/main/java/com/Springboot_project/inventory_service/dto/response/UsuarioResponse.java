package com.Springboot_project.inventory_service.dto.response;

import com.Springboot_project.inventory_service.model.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}