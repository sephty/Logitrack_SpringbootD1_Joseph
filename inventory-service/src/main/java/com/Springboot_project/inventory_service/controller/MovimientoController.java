package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.MovimientoRequest;
import com.Springboot_project.inventory_service.dto.response.MovimientoResponse;
import com.Springboot_project.inventory_service.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Entradas, salidas y transferencias de inventario")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    @Operation(
            summary = "Registrar un movimiento",
            description = "usuarioId es temporal: es el id del usuario responsable, tomado de /api/usuarios " +
                    "hasta que se conecte JWT/Spring Security."
    )
    public ResponseEntity<MovimientoResponse> registrar(
            @Valid @RequestBody MovimientoRequest dto,
            @Parameter(description = "Id del usuario responsable (temporal, sin JWT aun)")
            @RequestParam Long usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrarMovimiento(dto, usuarioId));
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos")
    public ResponseEntity<List<MovimientoResponse>> obtenerTodos() {
        return ResponseEntity.ok(movimientoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un movimiento por id")
    public ResponseEntity<MovimientoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    @GetMapping("/rango-fechas")
    @Operation(summary = "Movimientos entre dos fechas (formato ISO: 2026-08-01T00:00:00)")
    public ResponseEntity<List<MovimientoResponse>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.obtenerPorRangoFechas(inicio, fin));
    }

    @GetMapping("/reporte-general")
    @Operation(summary = "Resumen general: stock por bodega y productos mas movidos")
    public ResponseEntity<Map<String, Object>> obtenerResumenGeneral() {
        return ResponseEntity.ok(movimientoService.obtenerResumenGeneral());
    }
}