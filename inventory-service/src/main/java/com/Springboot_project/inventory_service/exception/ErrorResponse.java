package com.Springboot_project.inventory_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse (
        LocalDateTime timestamp, int status, String message, String errorCode
){}

