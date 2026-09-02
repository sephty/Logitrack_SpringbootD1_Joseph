package com.Springboot_project.inventory_service.service;

import com.Springboot_project.inventory_service.dto.request.ProductoRequest;
import com.Springboot_project.inventory_service.dto.response.ProductoResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {
    ProductoResponse guardar(ProductoRequest dto, Long usuarioId);
    List<ProductoResponse> obtenerTodas();
    ProductoResponse obtenerPorId(Long id);
    ProductoResponse actualizarProducto(Long id, ProductoRequest dto, Long usuarioId);
    void eliminarProducto(Long id, Long usuarioId);
    List<ProductoResponse> buscarPorNombre(String nombre);
    List<ProductoResponse> filtrarPrecioVentaMayorOIgualQue(BigDecimal precio);
    List<ProductoResponse> filtrarPrecioVentaMenorOIgualQue(BigDecimal precio);
    List<ProductoResponse> filtrarPrecioVentaEntre(BigDecimal precio1, BigDecimal precio2);
    List<ProductoResponse> filtrarPorNombreYPrecioVentaMayorOIgualQue(String nombre, BigDecimal precio);
    List<ProductoResponse> obtenerStockBajo();
}