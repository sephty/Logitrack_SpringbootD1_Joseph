package com.Springboot_project.inventory_service.controller;

import com.Springboot_project.inventory_service.dto.request.LoginRequest;
import com.Springboot_project.inventory_service.dto.request.UsuarioRequest;
import com.Springboot_project.inventory_service.dto.response.AuthResponse;
import com.Springboot_project.inventory_service.dto.response.UsuarioResponse;
import com.Springboot_project.inventory_service.model.RolUsuario;
import com.Springboot_project.inventory_service.security.CustomUserDetails;
import com.Springboot_project.inventory_service.security.JwtService;
import com.Springboot_project.inventory_service.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Login y registro publico. No requieren token.")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Valida credenciales y retorna un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, token retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas", content = @Content)
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        String rol = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(), rol));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Registrar un usuario (auto-registro publico)",
            description = "Cualquiera puede registrarse aqui, pero SIEMPRE queda con rol EMPLEADO " +
                    "sin importar lo que venga en el body. Para crear un ADMIN, un ADMIN ya autenticado " +
                    "debe usar POST /api/usuarios."
    )
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioRequest dto) {
        dto.setRol(RolUsuario.EMPLEADO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto, null));
    }
}