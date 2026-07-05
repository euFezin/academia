package br.senac.academia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Só processa se houver um token Bearer
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);

                // Valida o token antes de processar
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsername(token);
                    List<String> roles = jwtUtil.getRoles(token);

                    // Se username existe, configura autenticação mesmo se roles forem null
                    if (username != null) {
                        // Verifica se já não há autenticação no contexto
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            List<SimpleGrantedAuthority> authorities;
                            
                            if (roles != null && !roles.isEmpty()) {
                                authorities = roles.stream()
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                        .collect(Collectors.toList());
                            } else {
                                // Se não houver roles, cria lista vazia
                                authorities = new java.util.ArrayList<>();
                            }

                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                            username,
                                            null,
                                            authorities
                                    );

                            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
                // Se o token for inválido, não faz nada - deixa o Spring Security decidir
            } catch (Exception e) {
                // Em caso de erro ao processar o token, limpa o contexto mas continua
                // Não bloqueia a requisição - deixa o Spring Security decidir
                SecurityContextHolder.clearContext();
            }
        }

        // Continua a cadeia de filtros - o Spring Security decidirá se a requisição precisa de autenticação
        filterChain.doFilter(request, response);
    }
}