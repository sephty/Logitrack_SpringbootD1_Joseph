package com.Springboot_project.inventory_service.service.impl;

import com.Springboot_project.inventory_service.dto.request.ProductoRequest;
import com.Springboot_project.inventory_service.dto.response.ProductoResponse;
import com.Springboot_project.inventory_service.mapper.ProductoMapper;
import com.Springboot_project.inventory_service.model.Producto;
import com.Springboot_project.inventory_service.repository.ProductoRepository;
import com.Springboot_project.inventory_service.service.ContextService;
import com.Springboot_project.inventory_service.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private static final long UMBRAL_STOCK_BAJO = 10;

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final ContextService contextService;

    @Override
    @Transactional
    public ProductoResponse guardar(ProductoRequest dto, Long usuarioId) {
        contextService.setUsuarioActual(usuarioId);
        Producto producto = productoMapper.dtoToEntity(dto);
        return productoMapper.entityToDto(productoRepository.save(producto));
    }

    @Override
    public List<ProductoResponse> obtenerTodas() {
        return productoRepository.findAll().stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.entityToDto(buscarProducto(id));
    }

    @Override
    @Transactional
    public ProductoResponse actualizarProducto(Long id, ProductoRequest dto, Long usuarioId) {
        contextService.setUsuarioActual(usuarioId);
        Producto producto = buscarProducto(id);
        productoMapper.updateEntityToDto(producto, dto);
        return productoMapper.entityToDto(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id, Long usuarioId) {
        contextService.setUsuarioActual(usuarioId);
        productoRepository.delete(buscarProducto(id));
    }

    @Override
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public List<ProductoResponse> filtrarPrecioVentaMayorOIgualQue(BigDecimal precio) {
        return productoRepository.findByPrecioGreaterThanEqual(precio).stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public List<ProductoResponse> filtrarPrecioVentaMenorOIgualQue(BigDecimal precio) {
        return productoRepository.findByPrecioLessThanEqual(precio).stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public List<ProductoResponse> filtrarPrecioVentaEntre(BigDecimal precio1, BigDecimal precio2) {
        return productoRepository.findByPrecioBetween(precio1, precio2).stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public List<ProductoResponse> filtrarPorNombreYPrecioVentaMayorOIgualQue(String nombre, BigDecimal precio) {
        return productoRepository.findByNombreContainingIgnoreCaseAndPrecioGreaterThanEqual(nombre, precio).stream().map(productoMapper::entityToDto).toList();
    }

    @Override
    public List<ProductoResponse> obtenerStockBajo() {
        return productoRepository.findByStockLessThan(UMBRAL_STOCK_BAJO).stream().map(productoMapper::entityToDto).toList();
    }

    private Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }
}