package com.Springboot_project.inventory_service.dto.response;

import com.Springboot_project.inventory_service.model.TipoMovimiento;
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
public class MovimientoResponse {
    private Long id;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private String usuarioResponsable;
    private String bodegaOrigen;
    private String bodegaDestino;
    private String observaciones;
    private List<MovimientoDetalleResponse> detalles;
}
