package com.Springboot_project.inventory_service.repository;

import com.Springboot_project.inventory_service.model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovimientoDetalleRepository extends JpaRepository<DetalleMovimiento, Long> {

    @Query("SELECT d.producto.id, d.producto.nombre, SUM(d.cantidad) " +
            "FROM DetalleMovimiento d " +
            "GROUP BY d.producto.id, d.producto.nombre " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> productosMasMovidos();

    @Query("SELECT m.bodegaDestino.id, m.bodegaDestino.nombre, COALESCE(SUM(d.cantidad), 0) " +
            "FROM DetalleMovimiento d JOIN d.movimiento m " +
            "WHERE m.bodegaDestino IS NOT NULL " +
            "GROUP BY m.bodegaDestino.id, m.bodegaDestino.nombre")
    List<Object[]> sumarEntradasPorBodega();

    @Query("SELECT m.bodegaOrigen.id, m.bodegaOrigen.nombre, COALESCE(SUM(d.cantidad), 0) " +
            "FROM DetalleMovimiento d JOIN d.movimiento m " +
            "WHERE m.bodegaOrigen IS NOT NULL " +
            "GROUP BY m.bodegaOrigen.id, m.bodegaOrigen.nombre")
    List<Object[]> sumarSalidasPorBodega();
}