package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.UsuarioRequest;
import com.Springboot_project.inventory_service.dto.response.UsuarioResponse;
import com.Springboot_project.inventory_service.model.RolUsuario;
import com.Springboot_project.inventory_service.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro y consulta de usuarios (sin login todavia)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Registrar un usuario")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioResponse>> obtenerTodas() {
        return ResponseEntity.ok(usuarioService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por id")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un usuario (baja logica)")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Buscar un usuario por username")
    public ResponseEntity<UsuarioResponse> buscarPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.buscarPorUsername(username));
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Listar usuarios por rol (ADMIN o EMPLEADO)")
    public ResponseEntity<List<UsuarioResponse>> buscarPorRol(@PathVariable RolUsuario rol) {
        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }
}