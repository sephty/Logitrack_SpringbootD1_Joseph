package com.Springboot_project.inventory_service.service;

import com.Springboot_project.inventory_service.dto.request.MovimientoRequest;
import com.Springboot_project.inventory_service.dto.response.MovimientoResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MovimientoService {
    MovimientoResponse registrarMovimiento(MovimientoRequest dto, Long usuarioId);
    List<MovimientoResponse> obtenerTodos();
    MovimientoResponse obtenerPorId(Long id);
    List<MovimientoResponse> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    Map<String, Object> obtenerResumenGeneral();
}
