package com.Springboot_project.inventory_service.mapper;

import com.Springboot_project.inventory_service.dto.request.UsuarioRequest;
import com.Springboot_project.inventory_service.dto.response.UsuarioResponse;
import com.Springboot_project.inventory_service.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse entityToDto(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }

    public Usuario dtoToEntity(UsuarioRequest dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword());
        usuario.setEmail(dto.getEmail());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setRol(dto.getRol());
        usuario.setActivo(true);
        return usuario;
    }
}