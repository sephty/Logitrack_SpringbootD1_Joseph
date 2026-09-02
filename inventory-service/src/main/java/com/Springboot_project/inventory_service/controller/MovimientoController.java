package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.MovimientoRequest;
import com.Springboot_project.inventory_service.dto.response.MovimientoResponse;
import com.Springboot_project.inventory_service.security.CustomUserDetails;
import com.Springboot_project.inventory_service.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    @Operation(
            summary = "Registrar un movimiento",
            description = "Registra una ENTRADA, SALIDA o TRANSFERENCIA de uno o mas productos. " +
                    "ENTRADA requiere bodegaDestinoId, SALIDA requiere bodegaOrigenId, TRANSFERENCIA requiere ambos. " +
                    "Actualiza el stock del producto y valida que haya stock suficiente en SALIDA/TRANSFERENCIA."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento registrado",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente o datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bodega o producto no encontrado", content = @Content)
    })
    public ResponseEntity<MovimientoResponse> registrar(
            @Valid @RequestBody MovimientoRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrarMovimiento(dto, userDetails.getId()));
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos")
    public ResponseEntity<List<MovimientoResponse>> obtenerTodos() {
        return ResponseEntity.ok(movimientoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un movimiento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con ese id", content = @Content)
    })
    public ResponseEntity<MovimientoResponse> obtenerPorId(
            @Parameter(description = "Id del movimiento", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    @GetMapping("/rango-fechas")
    @Operation(
            summary = "Movimientos entre dos fechas",
            description = "Filtra movimientos cuya fecha este dentro del rango [inicio, fin]. Formato ISO-8601: yyyy-MM-ddTHH:mm:ss."
    )
    public ResponseEntity<List<MovimientoResponse>> obtenerPorRangoFechas(
            @Parameter(description = "Fecha/hora inicial (inclusive)", example = "2026-08-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fecha/hora final (inclusive)", example = "2026-08-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.obtenerPorRangoFechas(inicio, fin));
    }

    @GetMapping("/reporte-general")
    @Operation(
            summary = "Resumen general de inventario",
            description = "Devuelve dos listas: stock total actual por bodega (sumando entradas y restando " +
                    "salidas/transferencias) y los productos con mayor cantidad movida historicamente."
    )
    public ResponseEntity<Map<String, Object>> obtenerResumenGeneral() {
        return ResponseEntity.ok(movimientoService.obtenerResumenGeneral());
    }
}