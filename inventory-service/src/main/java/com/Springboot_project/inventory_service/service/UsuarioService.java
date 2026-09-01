package com.Springboot_project.inventory_service.service;

import com.Springboot_project.inventory_service.dto.request.UsuarioRequest;
import com.Springboot_project.inventory_service.dto.response.UsuarioResponse;
import com.Springboot_project.inventory_service.model.RolUsuario;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse registrar(UsuarioRequest dto);
    List<UsuarioResponse> obtenerTodas();
    UsuarioResponse obtenerPorId(Long id);
    void desactivarUsuario(Long id);
    UsuarioResponse buscarPorUsername(String username);
    List<UsuarioResponse> buscarPorRol(RolUsuario rol);
}