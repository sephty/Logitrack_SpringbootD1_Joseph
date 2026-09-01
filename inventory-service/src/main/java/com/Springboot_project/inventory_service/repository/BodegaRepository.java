package com.Springboot_project.inventory_service.repository;

import com.Springboot_project.inventory_service.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {
    List<Bodega> findByNombreContainingIgnoreCase(String nombre);
    List<Bodega> findByUbicacionContainingIgnoreCase(String ubicacion);
}