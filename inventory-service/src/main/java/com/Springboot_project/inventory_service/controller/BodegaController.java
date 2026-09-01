package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.BodegaRequest;
import com.Springboot_project.inventory_service.dto.response.BodegaResponse;
import com.Springboot_project.inventory_service.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
@Tag(name = "Bodegas", description = "CRUD de bodegas")
public class BodegaController {

    private final BodegaService bodegaService;

    @PostMapping
    @Operation(summary = "Crear una bodega")
    public ResponseEntity<BodegaResponse> guardar(@Valid @RequestBody BodegaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.guardar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas las bodegas")
    public ResponseEntity<List<BodegaResponse>> obtenerTodas() {
        return ResponseEntity.ok(bodegaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una bodega por id")
    public ResponseEntity<BodegaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una bodega")
    public ResponseEntity<BodegaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody BodegaRequest dto) {
        return ResponseEntity.ok(bodegaService.actualizarBodega(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una bodega")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bodegaService.eliminarBodega(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar bodegas por nombre")
    public ResponseEntity<List<BodegaResponse>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(bodegaService.buscarPorNombre(nombre));
    }

    @GetMapping("/ubicacion")
    @Operation(summary = "Buscar bodegas por ubicacion")
    public ResponseEntity<List<BodegaResponse>> buscarPorUbicacion(@RequestParam String ubicacion) {
        return ResponseEntity.ok(bodegaService.buscarPorUbicacion(ubicacion));
    }
}