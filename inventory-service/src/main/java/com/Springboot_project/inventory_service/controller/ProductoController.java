package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.ProductoRequest;
import com.Springboot_project.inventory_service.dto.response.ProductoResponse;
import com.Springboot_project.inventory_service.security.CustomUserDetails;
import com.Springboot_project.inventory_service.service.ProductoService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @Operation(
            summary = "Crear un producto",
            description = "Registra un nuevo producto en el catalogo. La creacion queda auditada " +
                    "automaticamente (trigger de MySQL) contra el usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (campos requeridos, formatos)", content = @Content)
    })
    public ResponseEntity<ProductoResponse> guardar(
            @Valid @RequestBody ProductoRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(dto, userDetails.getId()));
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna el catalogo completo, sin filtros.")
    @ApiResponse(responseCode = "200", description = "Lista de productos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @Schema(implementation = ProductoResponse.class))))
    public ResponseEntity<List<ProductoResponse>> obtenerTodas() {
        return ResponseEntity.ok(productoService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un producto con ese id", content = @Content)
    })
    public ResponseEntity<ProductoResponse> obtenerPorId(
            @Parameter(description = "Id del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un producto",
            description = "Reemplaza nombre, categoria, stock y precio del producto. Queda auditado contra el usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<ProductoResponse> actualizar(
            @Parameter(description = "Id del producto a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductoRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto, userDetails.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un producto",
            description = "Borrado fisico. Queda auditado (tipo DELETE) contra el usuario autenticado antes de eliminarse."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id del producto a eliminar", example = "1") @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        productoService.eliminarProducto(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    @Operation(
            summary = "Productos con stock bajo",
            description = "Retorna los productos con stock menor a 10 unidades (umbral fijo del sistema)."
    )
    public ResponseEntity<List<ProductoResponse>> obtenerStockBajo() {
        return ResponseEntity.ok(productoService.obtenerStockBajo());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre", description = "Coincidencia parcial, sin distinguir mayusculas/minusculas.")
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(
            @Parameter(description = "Texto a buscar dentro del nombre del producto", example = "laptop")
            @RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @GetMapping("/filtrar/precio-mayor-igual")
    @Operation(summary = "Productos con precio >= al indicado")
    public ResponseEntity<List<ProductoResponse>> filtrarPrecioMayorIgual(
            @Parameter(description = "Precio minimo (inclusive)", example = "100000") @RequestParam BigDecimal precio) {
        return ResponseEntity.ok(productoService.filtrarPrecioVentaMayorOIgualQue(precio));
    }

    @GetMapping("/filtrar/precio-menor-igual")
    @Operation(summary = "Productos con precio <= al indicado")
    public ResponseEntity<List<ProductoResponse>> filtrarPrecioMenorIgual(
            @Parameter(description = "Precio maximo (inclusive)", example = "500000") @RequestParam BigDecimal precio) {
        return ResponseEntity.ok(productoService.filtrarPrecioVentaMenorOIgualQue(precio));
    }

    @GetMapping("/filtrar/precio-entre")
    @Operation(summary = "Productos con precio entre dos valores")
    public ResponseEntity<List<ProductoResponse>> filtrarPrecioEntre(
            @Parameter(description = "Precio minimo (inclusive)", example = "100000") @RequestParam BigDecimal min,
            @Parameter(description = "Precio maximo (inclusive)", example = "1000000") @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productoService.filtrarPrecioVentaEntre(min, max));
    }

    @GetMapping("/filtrar/nombre-y-precio")
    @Operation(summary = "Productos por nombre y precio >= al indicado")
    public ResponseEntity<List<ProductoResponse>> filtrarPorNombreYPrecio(
            @Parameter(description = "Texto a buscar dentro del nombre", example = "mouse") @RequestParam String nombre,
            @Parameter(description = "Precio minimo (inclusive)", example = "50000") @RequestParam BigDecimal precio) {
        return ResponseEntity.ok(productoService.filtrarPorNombreYPrecioVentaMayorOIgualQue(nombre, precio));
    }
}