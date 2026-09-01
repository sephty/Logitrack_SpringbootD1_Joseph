package com.Springboot_project.inventory_service.mapper;

import com.Springboot_project.inventory_service.dto.response.AuditoriaDetalleResponse;
import com.Springboot_project.inventory_service.dto.response.AuditoriaResponse;
import com.Springboot_project.inventory_service.model.Auditoria;
import com.Springboot_project.inventory_service.model.AuditoriaDetalle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuditoriaMapper {

    public AuditoriaResponse entityToDto(Auditoria auditoria) {
        if (auditoria == null) return null;

        List<AuditoriaDetalleResponse> detalles = new ArrayList<>();
        List<AuditoriaDetalle> detallesEntidad = auditoria.getDetalles();
        for (int i = 0; i < detallesEntidad.size(); i++) {
            detalles.add(detalleToDto(detallesEntidad.get(i)));
        }

        return new AuditoriaResponse(
                auditoria.getId(),
                auditoria.getTipoOperacion(),
                auditoria.getFechaHora(),
                auditoria.getUsuario() != null ? auditoria.getUsuario().getNombreCompleto() : null,
                auditoria.getEntidadAfectada(),
                auditoria.getEntidadId(),
                detalles
        );
    }

    public AuditoriaDetalleResponse detalleToDto(AuditoriaDetalle detalle) {
        if (detalle == null) return null;
        return new AuditoriaDetalleResponse(
                detalle.getCampo(),
                detalle.getValorAnterior(),
                detalle.getValorNuevo()
        );
    }
}