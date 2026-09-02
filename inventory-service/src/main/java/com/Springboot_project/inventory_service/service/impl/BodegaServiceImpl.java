package com.Springboot_project.inventory_service.service.impl;

import com.Springboot_project.inventory_service.dto.request.BodegaRequest;
import com.Springboot_project.inventory_service.dto.response.BodegaResponse;
import com.Springboot_project.inventory_service.mapper.BodegaMapper;
import com.Springboot_project.inventory_service.model.Bodega;
import com.Springboot_project.inventory_service.repository.BodegaRepository;
import com.Springboot_project.inventory_service.service.BodegaService;
import com.Springboot_project.inventory_service.service.ContextService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final ContextService contextService;

    @Override
    @Transactional
    public BodegaResponse guardar(BodegaRequest dto, Long usuarioId) {
        contextService.setUsuarioActual(usuarioId);
        Bodega bodega = bodegaMapper.dtoToEntity(dto);
        return bodegaMapper.entityToDto(bodegaRepository.save(bodega));
    }

    @Override
    public List<BodegaResponse> obtenerTodas() {
        return bodegaRepository.findAll().stream().map(bodegaMapper::entityToDto).toList();
    }

    @Override
    public BodegaResponse obtenerPorId(Long id) {
        return bodegaMapper.entityToDto(buscarBodega(id));
    }

    @Override
    @Transactional
    public BodegaResponse actualizarBodega(Long id, BodegaRequest dto, Long usuarioId) {
        contextService.setUsuarioActual(usuarioId);
        Bodega bodega = buscarBodega(id);
        bodegaMapper.updateEntityToDto(bodega, dto);
        return bodegaMapper.entityToDto(bodegaRepository.save(bodega));
    }

    @Override
    @Transactional
    public void eliminarBodega(Long id, Long usuarioId) {
        contextService.setUsuarioActual(usuarioId);
        bodegaRepository.delete(buscarBodega(id));
    }

    @Override
    public List<BodegaResponse> buscarPorNombre(String nombre) {
        return bodegaRepository.findByNombreContainingIgnoreCase(nombre).stream().map(bodegaMapper::entityToDto).toList();
    }

    @Override
    public List<BodegaResponse> buscarPorUbicacion(String ubicacion) {
        return bodegaRepository.findByUbicacionContainingIgnoreCase(ubicacion).stream().map(bodegaMapper::entityToDto).toList();
    }

    private Bodega buscarBodega(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bodega no encontrada con id: " + id));
    }
}