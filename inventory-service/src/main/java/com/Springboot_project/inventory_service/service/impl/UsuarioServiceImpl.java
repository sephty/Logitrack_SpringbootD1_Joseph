package com.Springboot_project.inventory_service.service.impl;

import com.Springboot_project.inventory_service.dto.request.UsuarioRequest;
import com.Springboot_project.inventory_service.dto.response.UsuarioResponse;
import com.Springboot_project.inventory_service.exception.BusinessRuleException;
import com.Springboot_project.inventory_service.mapper.UsuarioMapper;
import com.Springboot_project.inventory_service.model.RolUsuario;
import com.Springboot_project.inventory_service.model.Usuario;
import com.Springboot_project.inventory_service.repository.UsuarioRepository;
import com.Springboot_project.inventory_service.service.ContextService;
import com.Springboot_project.inventory_service.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final ContextService contextService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse registrar(UsuarioRequest dto, Long usuarioIdCreador) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessRuleException("El username ya esta en uso");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("El email ya esta en uso");
        }

        // usuarioIdCreador puede ser null en el primer registro (bootstrap del admin inicial),
        // ya que aun no existe nadie autenticado para crearlo.
        contextService.setUsuarioActual(usuarioIdCreador);

        Usuario usuario = usuarioMapper.dtoToEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        return usuarioMapper.entityToDto(usuarioRepository.save(usuario));
    }

    @Override
    public List<UsuarioResponse> obtenerTodas() {
        return usuarioRepository.findAll().stream().map(usuarioMapper::entityToDto).toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.entityToDto(buscarUsuario(id));
    }

    @Override
    @Transactional
    public void desactivarUsuario(Long id, Long usuarioIdEjecutor) {
        contextService.setUsuarioActual(usuarioIdEjecutor);
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
        return usuarioRepository.findByRol(rol).stream().map(usuarioMapper::entityToDto).toList();
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }
}