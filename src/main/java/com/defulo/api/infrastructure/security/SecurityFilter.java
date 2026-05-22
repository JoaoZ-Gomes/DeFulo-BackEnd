package com.defulo.api.infrastructure.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerExceptionResolver;
import com.defulo.api.features.usuario.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository repository;
    private final HandlerExceptionResolver resolver;

    public SecurityFilter(TokenService tokenService,
                          UsuarioRepository repository,
                          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.tokenService = tokenService;
        this.repository = repository;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tokenJWT = recuperarToken(request);

            if (tokenJWT != null) {
                String subject = tokenService.getSubject(tokenJWT);
                UserDetails usuario = (UserDetails) repository.findByEmail(subject).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            resolver.resolveException(request, response, null, ex);
            return; // Stop filter chain if authentication fails
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
