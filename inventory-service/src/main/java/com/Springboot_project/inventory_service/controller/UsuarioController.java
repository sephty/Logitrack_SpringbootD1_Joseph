package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.UsuarioRequest;
import com.Springboot_project.inventory_service.dto.response.UsuarioResponse;
import com.Springboot_project.inventory_service.model.RolUsuario;
import com.Springboot_project.inventory_service.security.CustomUserDetails;
import com.Springboot_project.inventory_service.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(
            summary = "Registrar un usuario",
            description = "Crea un usuario nuevo (ADMIN o EMPLEADO) respetando el rol del body. Solo ADMIN. " +
                    "El id del admin que registra se toma del token, nunca de un parametro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Username o email ya registrado, o datos invalidos", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody UsuarioRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto, userDetails.getId()));
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioResponse>> obtenerTodas() {
        return ResponseEntity.ok(usuarioService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(
            summary = "Desactivar un usuario",
            description = "Baja logica: pone activo=false, no elimina el registro (necesario para conservar el historial de auditoria)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario desactivado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    public ResponseEntity<Void> desactivar(
            @Parameter(description = "Id del usuario a desactivar", example = "3") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        usuarioService.desactivarUsuario(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Buscar un usuario por username", description = "Coincidencia exacta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese username", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> buscarPorUsername(
            @Parameter(description = "Username exacto", example = "empleado1") @PathVariable String username) {
        return ResponseEntity.ok(usuarioService.buscarPorUsername(username));
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Listar usuarios por rol")
    public ResponseEntity<List<UsuarioResponse>> buscarPorRol(
            @Parameter(description = "Rol a filtrar") @PathVariable RolUsuario rol) {
        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }
}