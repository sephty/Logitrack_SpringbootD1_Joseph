package com.Springboot_project.inventory_service.mapper;

import com.Springboot_project.inventory_service.dto.response.MovimientoDetalleResponse;
import com.Springboot_project.inventory_service.dto.response.MovimientoResponse;
import com.Springboot_project.inventory_service.model.DetalleMovimiento;
import com.Springboot_project.inventory_service.model.Movimiento;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MovimientoMapper {

    public MovimientoResponse entityToDto(Movimiento movimiento) {
        if (movimiento == null) return null;

        List<MovimientoDetalleResponse> detalles = new ArrayList<>();
        List<DetalleMovimiento> detallesEntidad = movimiento.getDetalles();
        for (int i = 0; i < detallesEntidad.size(); i++) {
            detalles.add(detalleToDto(detallesEntidad.get(i)));
        }

        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                movimiento.getUsuario().getNombreCompleto(),
                movimiento.getBodegaOrigen() != null ? movimiento.getBodegaOrigen().getNombre() : null,
                movimiento.getBodegaDestino() != null ? movimiento.getBodegaDestino().getNombre() : null,
                movimiento.getObservaciones(),
                detalles
        );
    }

    public MovimientoDetalleResponse detalleToDto(DetalleMovimiento detalle) {
        if (detalle == null) return null;
        return new MovimientoDetalleResponse(
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad()
        );
    }
}
