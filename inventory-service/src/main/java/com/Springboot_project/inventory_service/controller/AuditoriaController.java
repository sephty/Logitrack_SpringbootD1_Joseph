package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.response.AuditoriaResponse;
import com.Springboot_project.inventory_service.model.TipoOperacion;
import com.Springboot_project.inventory_service.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@Tag(name = "Auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    @Operation(
            summary = "Listar toda la auditoria",
            description = "Cada registro incluye el detalle campo por campo de lo que cambio " +
                    "(valor anterior y nuevo), generado automaticamente por triggers de MySQL."
    )
    public ResponseEntity<List<AuditoriaResponse>> obtenerTodas() {
        return ResponseEntity.ok(auditoriaService.obtenerTodas());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Auditoria filtrada por usuario responsable")
    public ResponseEntity<List<AuditoriaResponse>> obtenerPorUsuario(
            @Parameter(description = "Id del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(auditoriaService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/tipo/{tipoOperacion}")
    @Operation(summary = "Auditoria filtrada por tipo de operacion")
    public ResponseEntity<List<AuditoriaResponse>> obtenerPorTipoOperacion(
            @Parameter(description = "Tipo de operacion") @PathVariable TipoOperacion tipoOperacion) {
        return ResponseEntity.ok(auditoriaService.obtenerPorTipoOperacion(tipoOperacion));
    }
}