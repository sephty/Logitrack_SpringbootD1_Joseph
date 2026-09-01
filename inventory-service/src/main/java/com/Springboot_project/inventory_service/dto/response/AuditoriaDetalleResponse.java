package com.Springboot_project.inventory_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaDetalleResponse {
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
}