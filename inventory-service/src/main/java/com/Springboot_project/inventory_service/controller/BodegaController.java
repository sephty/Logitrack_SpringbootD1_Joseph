package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.BodegaRequest;
import com.Springboot_project.inventory_service.dto.response.BodegaResponse;
import com.Springboot_project.inventory_service.security.CustomUserDetails;
import com.Springboot_project.inventory_service.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
@Tag(name = "Bodegas")
public class BodegaController {

    private final BodegaService bodegaService;

    @PostMapping
    @Operation(
            summary = "Crear una bodega",
            description = "Registra una nueva bodega. Se crea activa por defecto y queda auditada contra el usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bodega creada",
                    content = @Content(schema = @Schema(implementation = BodegaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content)
    })
    public ResponseEntity<BodegaResponse> guardar(
            @Valid @RequestBody BodegaRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.guardar(dto, userDetails.getId()));
    }

    @GetMapping
    @Operation(summary = "Listar todas las bodegas")
    public ResponseEntity<List<BodegaResponse>> obtenerTodas() {
        return ResponseEntity.ok(bodegaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una bodega por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bodega encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una bodega con ese id", content = @Content)
    })
    public ResponseEntity<BodegaResponse> obtenerPorId(
            @Parameter(description = "Id de la bodega", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una bodega", description = "Actualiza nombre, ubicacion, capacidad y encargado. Queda auditado contra el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bodega actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    public ResponseEntity<BodegaResponse> actualizar(
            @Parameter(description = "Id de la bodega a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody BodegaRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bodegaService.actualizarBodega(id, dto, userDetails.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una bodega", description = "Borrado fisico. Queda auditado (tipo DELETE) contra el usuario autenticado antes de eliminarse.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bodega eliminada"),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id de la bodega a eliminar", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        bodegaService.eliminarBodega(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar bodegas por nombre", description = "Coincidencia parcial, sin distinguir mayusculas/minusculas.")
    public ResponseEntity<List<BodegaResponse>> buscarPorNombre(
            @Parameter(description = "Texto a buscar dentro del nombre de la bodega", example = "central")
            @RequestParam String nombre) {
        return ResponseEntity.ok(bodegaService.buscarPorNombre(nombre));
    }

    @GetMapping("/ubicacion")
    @Operation(summary = "Buscar bodegas por ubicacion", description = "Coincidencia parcial, sin distinguir mayusculas/minusculas.")
    public ResponseEntity<List<BodegaResponse>> buscarPorUbicacion(
            @Parameter(description = "Texto a buscar dentro de la ubicacion", example = "Bogota")
            @RequestParam String ubicacion) {
        return ResponseEntity.ok(bodegaService.buscarPorUbicacion(ubicacion));
    }
}