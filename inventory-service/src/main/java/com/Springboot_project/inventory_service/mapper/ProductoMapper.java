package com.Springboot_project.inventory_service.mapper;

import com.Springboot_project.inventory_service.dto.request.ProductoRequest;
import com.Springboot_project.inventory_service.dto.response.ProductoResponse;
import com.Springboot_project.inventory_service.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoResponse entityToDto(Producto producto) {
        if (producto == null) return null;
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getStock(),
                producto.getPrecio(),
                producto.getFechaCreacion()
        );
    }

    public Producto dtoToEntity(ProductoRequest dto) {
        if (dto == null) return null;
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setStock(dto.getStock());
        producto.setPrecio(dto.getPrecio());
        return producto;
    }

    public void updateEntityToDto(Producto producto, ProductoRequest dto) {
        if (dto == null || producto == null) return;
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setStock(dto.getStock());
        producto.setPrecio(dto.getPrecio());
    }
}