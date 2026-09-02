package com.Springboot_project.inventory_service.service;

import com.Springboot_project.inventory_service.dto.request.BodegaRequest;
import com.Springboot_project.inventory_service.dto.response.BodegaResponse;

import java.util.List;

public interface BodegaService {
    BodegaResponse guardar(BodegaRequest dto, Long usuarioId);
    List<BodegaResponse> obtenerTodas();
    BodegaResponse obtenerPorId(Long id);
    BodegaResponse actualizarBodega(Long id, BodegaRequest dto, Long usuarioId);
    void eliminarBodega(Long id, Long usuarioId);
    List<BodegaResponse> buscarPorNombre(String nombre);
    List<BodegaResponse> buscarPorUbicacion(String ubicacion);
}