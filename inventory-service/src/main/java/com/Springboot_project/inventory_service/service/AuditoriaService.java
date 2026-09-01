package com.Springboot_project.inventory_service.service;

import com.Springboot_project.inventory_service.dto.response.AuditoriaResponse;
import com.Springboot_project.inventory_service.model.TipoOperacion;

import java.util.List;

public interface AuditoriaService {
    List<AuditoriaResponse> obtenerTodas();
    List<AuditoriaResponse> obtenerPorUsuario(Long usuarioId);
    List<AuditoriaResponse> obtenerPorTipoOperacion(TipoOperacion tipoOperacion);
}