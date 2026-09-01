package com.Springboot_project.inventory_service.service.impl;

import com.Springboot_project.inventory_service.dto.request.ProductoRequest;
import com.Springboot_project.inventory_service.dto.response.ProductoResponse;
import com.Springboot_project.inventory_service.mapper.ProductoMapper;
import com.Springboot_project.inventory_service.model.Producto;
import com.Springboot_project.inventory_service.repository.ProductoRepository;
import com.Springboot_project.inventory_service.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private static final long UMBRAL_STOCK_BAJO = 10;

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public ProductoResponse guardar(ProductoRequest dto) {
        Producto producto = productoMapper.dtoToEntity(dto);
        return productoMapper.entityToDto(productoRepository.save(producto));
    }

    @Override
    public List<ProductoResponse> obtenerTodas() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.entityToDto(buscarProducto(id));
    }

    @Override
    public ProductoResponse actualizarProducto(Long id, ProductoRequest dto) {
        Producto producto = buscarProducto(id);
        productoMapper.updateEntityToDto(producto, dto);
        return productoMapper.entityToDto(productoRepository.save(producto));
    }

    @Override
    public void eliminarProducto(Long id) {
        productoRepository.delete(buscarProducto(id));
    }

    @Override
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Override
    public List<ProductoResponse> filtrarPrecioVentaMayorOIgualQue(BigDecimal precio) {
        return productoRepository.findByPrecioGreaterThanEqual(precio)
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Override
    public List<ProductoResponse> filtrarPrecioVentaMenorOIgualQue(BigDecimal precio) {
        return productoRepository.findByPrecioLessThanEqual(precio)
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Override
    public List<ProductoResponse> filtrarPrecioVentaEntre(BigDecimal precio1, BigDecimal precio2) {
        return productoRepository.findByPrecioBetween(precio1, precio2)
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Override
    public List<ProductoResponse> filtrarPorNombreYPrecioVentaMayorOIgualQue(String nombre, BigDecimal precio) {
        return productoRepository.findByNombreContainingIgnoreCaseAndPrecioGreaterThanEqual(nombre, precio)
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Override
    public List<ProductoResponse> obtenerStockBajo() {
        return productoRepository.findByStockLessThan(UMBRAL_STOCK_BAJO)
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }
}