package com.Springboot_project.inventory_service.dto.response;

import com.Springboot_project.inventory_service.model.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaResponse {
    private Long id;
    private TipoOperacion tipoOperacion;
    private LocalDateTime fechaHora;
    private String usuario;
    private String entidadAfectada;
    private Long entidadId;
    private List<AuditoriaDetalleResponse> detalles;
}
