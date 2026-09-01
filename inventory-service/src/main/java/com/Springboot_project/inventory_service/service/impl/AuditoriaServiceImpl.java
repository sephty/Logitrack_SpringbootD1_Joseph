package com.Springboot_project.inventory_service.service.impl;

import com.Springboot_project.inventory_service.dto.response.AuditoriaResponse;
import com.Springboot_project.inventory_service.mapper.AuditoriaMapper;
import com.Springboot_project.inventory_service.model.TipoOperacion;
import com.Springboot_project.inventory_service.repository.AuditoriaRepository;
import com.Springboot_project.inventory_service.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;

    @Override
    public List<AuditoriaResponse> obtenerTodas() {
        return auditoriaRepository.findAll()
                .stream()
                .map(auditoriaMapper::entityToDto)
                .toList();
    }

    @Override
    public List<AuditoriaResponse> obtenerPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(auditoriaMapper::entityToDto)
                .toList();
    }

    @Override
    public List<AuditoriaResponse> obtenerPorTipoOperacion(TipoOperacion tipoOperacion) {
        return auditoriaRepository.findByTipoOperacion(tipoOperacion)
                .stream()
                .map(auditoriaMapper::entityToDto)
                .toList();
    }
}