package com.Springboot_project.inventory_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logiTrackOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiTrack API")
                        .description("Sistema de gestion de bodegas y auditoria de LogiTrack S.A. " +
                                "Permite administrar bodegas, productos, movimientos de inventario " +
                                "(entradas, salidas y transferencias) y consultar la auditoria automatica " +
                                "generada por triggers de base de datos.\n\n" +
                                "Seguridad: JWT bearer token. Login en POST /auth/login, registro publico " +
                                "(rol EMPLEADO forzado) en POST /auth/register. El resto de los endpoints " +
                                "toma el usuario responsable del token, no de un query param. Usa el boton " +
                                "Authorize con el token obtenido en el login.")
                        .version("v0.1"))
                .tags(List.of(
                        new Tag().name("Auth").description("Login y registro publico"),
                        new Tag().name("Productos").description("CRUD y filtros de productos del inventario"),
                        new Tag().name("Bodegas").description("CRUD de bodegas distribuidas por ciudad"),
                        new Tag().name("Usuarios").description("Registro y consulta de usuarios (ADMIN / EMPLEADO)"),
                        new Tag().name("Movimientos").description("Entradas, salidas y transferencias de inventario entre bodegas"),
                        new Tag().name("Auditoria").description("Consulta de cambios registrados automaticamente por triggers de MySQL")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}