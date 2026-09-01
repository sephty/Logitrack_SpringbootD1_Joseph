package com.Springboot_project.inventory_service.dto.request;

import com.Springboot_project.inventory_service.model.TipoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    private Long bodegaOrigenId;

    private Long bodegaDestinoId;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    @NotEmpty(message = "El movimiento debe tener al menos un producto")
    @Valid
    private List<MovimientoDetalleRequest> detalles;
}