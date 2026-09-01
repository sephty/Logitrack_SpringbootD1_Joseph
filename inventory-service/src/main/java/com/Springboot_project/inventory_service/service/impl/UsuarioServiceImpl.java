package com.Springboot_project.inventory_service.service.impl;

import com.Springboot_project.inventory_service.dto.request.UsuarioRequest;
import com.Springboot_project.inventory_service.dto.response.UsuarioResponse;
import com.Springboot_project.inventory_service.exception.BusinessRuleException;
import com.Springboot_project.inventory_service.mapper.UsuarioMapper;
import com.Springboot_project.inventory_service.model.RolUsuario;
import com.Springboot_project.inventory_service.model.Usuario;
import com.Springboot_project.inventory_service.repository.UsuarioRepository;
import com.Springboot_project.inventory_service.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioResponse registrar(UsuarioRequest dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessRuleException("El username ya esta en uso");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("El email ya esta en uso");
        }

        Usuario usuario = usuarioMapper.dtoToEntity(dto);
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }

    @Override
    public List<UsuarioResponse> obtenerTodas() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::entityToDto)
                .toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.entityToDto(buscarUsuario(id));
    }

    @Override
    public void desactivarUsuario(Long id) {
        Usuario usuario = buscarUsuario(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioResponse buscarPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con username: " + username));
        return usuarioMapper.entityToDto(usuario);
    }

    @Override
    public List<UsuarioResponse> buscarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol)
                .stream()
                .map(usuarioMapper::entityToDto)
                .toList();
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }
}