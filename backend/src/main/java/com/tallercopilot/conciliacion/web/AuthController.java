package com.tallercopilot.conciliacion.web;

import com.tallercopilot.conciliacion.security.JwtProvider;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtProvider jwtProvider;

    public AuthController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // Usuarios en memoria para el taller — reemplazar con UserDetailsService en produccion
        String role = resolveRole(username, password);

        String token = jwtProvider.generate(username, List.of(role));
        return Map.of("token", token, "role", role);
    }

    private String resolveRole(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            return "ADMIN";
        }
        if ("lectura".equals(username) && "lectura123".equals(password)) {
            return "LECTURA";
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
    }
}
