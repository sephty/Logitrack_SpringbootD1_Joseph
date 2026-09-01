package com.Springboot_project.inventory_service.repository;

import com.Springboot_project.inventory_service.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}