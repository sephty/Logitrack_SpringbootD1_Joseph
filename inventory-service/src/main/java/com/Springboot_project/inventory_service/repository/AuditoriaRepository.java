package com.Springboot_project.inventory_service.repository;

import com.Springboot_project.inventory_service.model.Auditoria;
import com.Springboot_project.inventory_service.model.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByUsuarioId(Long usuarioId);
    List<Auditoria> findByTipoOperacion(TipoOperacion tipoOperacion);
}