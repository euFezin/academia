package br.senac.academia.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/auth/login",
            "/auth/validate",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/swagger-ui/index.html",
            "/h2-console/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        String requestPath = request.getRequestURI();
        
        // Verifica se o endpoint é público
        boolean isPublicEndpoint = PUBLIC_ENDPOINTS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
        
        // Se for endpoint público, não retorna erro de autenticação
        // Deixa o Spring Security processar normalmente
        if (isPublicEndpoint) {
            return;
        }
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"erro\": \"Token inválido ou ausente. Faça login novamente.\"}");
    }
}

