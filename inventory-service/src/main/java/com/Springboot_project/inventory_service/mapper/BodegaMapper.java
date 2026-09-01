package com.Springboot_project.inventory_service.mapper;

import com.Springboot_project.inventory_service.dto.request.BodegaRequest;
import com.Springboot_project.inventory_service.dto.response.BodegaResponse;
import com.Springboot_project.inventory_service.model.Bodega;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public BodegaResponse entityToDto(Bodega bodega) {
        if (bodega == null) return null;
        return new BodegaResponse(
                bodega.getId(),
                bodega.getNombre(),
                bodega.getUbicacion(),
                bodega.getCapacidad(),
                bodega.getEncargado(),
                bodega.getActivo(),
                bodega.getFechaCreacion()
        );
    }

    public Bodega dtoToEntity(BodegaRequest dto) {
        if (dto == null) return null;
        Bodega bodega = new Bodega();
        bodega.setNombre(dto.getNombre());
        bodega.setUbicacion(dto.getUbicacion());
        bodega.setCapacidad(dto.getCapacidad());
        bodega.setEncargado(dto.getEncargado());
        bodega.setActivo(true);
        return bodega;
    }

    public void updateEntityToDto(Bodega bodega, BodegaRequest dto) {
        if (dto == null || bodega == null) return;
        bodega.setNombre(dto.getNombre());
        bodega.setUbicacion(dto.getUbicacion());
        bodega.setCapacidad(dto.getCapacidad());
        bodega.setEncargado(dto.getEncargado());
    }
}