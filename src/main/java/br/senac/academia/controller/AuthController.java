package br.senac.academia.controller;

import br.senac.academia.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credenciais) {
        try {
            String username = credenciais.get("username");
            String password = credenciais.get("password");

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("erro", "Username e senha são obrigatórios");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Define no SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Extrai as roles
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(authority -> authority.replace("ROLE_", ""))
                    .collect(Collectors.toList());

            // Gera o token
            String token = jwtUtil.generateToken(authentication.getName(), roles);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);
            response.put("roles", roles);
            response.put("tipo", "Bearer");

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao processar autenticação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("valido", false);
            response.put("erro", "Token não fornecido");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isValid = jwtUtil.validateToken(token);
        Map<String, Object> response = new HashMap<>();
        response.put("valido", isValid);

        if (isValid) {
            response.put("username", jwtUtil.getUsername(token));
            response.put("roles", jwtUtil.getRoles(token));
            response.put("expirado", jwtUtil.isTokenExpired(token));
        } else {
            response.put("erro", "Token inválido ou expirado");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }
}