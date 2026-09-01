package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.ProductoRequest;
import com.Springboot_project.inventory_service.dto.response.ProductoResponse;
import com.Springboot_project.inventory_service.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "CRUD y filtros de productos")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @Operation(summary = "Crear un producto")
    public ResponseEntity<ProductoResponse> guardar(@Valid @RequestBody ProductoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public ResponseEntity<List<ProductoResponse>> obtenerTodas() {
        return ResponseEntity.ok(productoService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por id")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Productos con stock menor a 10 unidades")
    public ResponseEntity<List<ProductoResponse>> obtenerStockBajo() {
        return ResponseEntity.ok(productoService.obtenerStockBajo());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre (contiene, ignora mayus/minus)")
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/filtrar/precio-mayor-igual")
    @Operation(summary = "Productos con precio >= al indicado")
    public ResponseEntity<List<ProductoResponse>> filtrarPrecioMayorIgual(@RequestParam BigDecimal precio) {
        return ResponseEntity.ok(productoService.filtrarPrecioVentaMayorOIgualQue(precio));
    }

    @GetMapping("/filtrar/precio-menor-igual")
    @Operation(summary = "Productos con precio <= al indicado")
    public ResponseEntity<List<ProductoResponse>> filtrarPrecioMenorIgual(@RequestParam BigDecimal precio) {
        return ResponseEntity.ok(productoService.filtrarPrecioVentaMenorOIgualQue(precio));
    }

    @GetMapping("/filtrar/precio-entre")
    @Operation(summary = "Productos con precio entre dos valores")
    public ResponseEntity<List<ProductoResponse>> filtrarPrecioEntre(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productoService.filtrarPrecioVentaEntre(min, max));
    }

    @GetMapping("/filtrar/nombre-y-precio")
    @Operation(summary = "Productos por nombre y precio >= al indicado")
    public ResponseEntity<List<ProductoResponse>> filtrarPorNombreYPrecio(
            @RequestParam String nombre, @RequestParam BigDecimal precio) {
        return ResponseEntity.ok(productoService.filtrarPorNombreYPrecioVentaMayorOIgualQue(nombre, precio));
    }
}