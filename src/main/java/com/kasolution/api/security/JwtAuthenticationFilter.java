package com.kasolution.api.security;

import com.kasolution.api.exception.CredenciaisException;
import com.kasolution.api.model.Role;

import com.kasolution.api.model.Usuario;
import com.kasolution.api.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    Claims claims = jwtService.parseToken(token);
                    UUID userId = UUID.fromString(claims.getSubject());
                    String roleStr = claims.get("role", String.class);
                    Role role = Role.valueOf(roleStr);
                    String email = claims.get("email", String.class);

                    Usuario user = userRepository.findById(userId)
                            .orElseThrow( () -> new CredenciaisException("Usuario nao encontrado "));

                    if(!user.isActive()) {
                        filterChain.doFilter(request,response);
                        return ;
                    }
                    UUID gerenteId ;
                    if(user.isGerente()){
                        gerenteId = user.getId();
                    }else{
                        gerenteId = user.getGerenteid();
                    }



                    UsuarioAutenticado principal = new UsuarioAutenticado(userId, email, role, gerenteId);
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (JwtException | IllegalArgumentException ex) {
                    throw  new CredenciaisException("Token inválido + "+ex);
                }
            }
        }
        filterChain.doFilter(request, response);
    }



}
