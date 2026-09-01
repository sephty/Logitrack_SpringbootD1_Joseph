package com.Springboot_project.inventory_service.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class ContextService {

    @PersistenceContext
    private EntityManager entityManager;

    public void setUsuarioActual(Long usuarioId) {
        entityManager.createNativeQuery("SET @usuario_actual_id = :usuarioId")
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();
    }
}