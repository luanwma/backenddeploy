package com.kasolution.api.config;

import com.kasolution.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Habilita o CORS usando as configurações do bean abaixo
        http.cors(Customizer.withDefaults());

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/login",
                        "/api/comprar_acesso",
                        "/api/gerente/registro",
                        "/api/desenvolvedor/registro",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/h2-console/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tarefas/criar").hasRole("GERENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/tarefas/**").hasRole("GERENTE")
                .requestMatchers(HttpMethod.POST, "/api/tarefas/editar/**").hasAnyRole("GERENTE", "DESENVOLVEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/tarefas/**").hasAnyRole("GERENTE", "DESENVOLVEDOR")
                .requestMatchers(HttpMethod.GET, "/api/tarefas/**").hasAnyRole("GERENTE","DESENVOLVEDOR","ADMIN")
                .requestMatchers(HttpMethod.POST,
                        "/api/tarefas/*/iniciar",
                        "/api/tarefas/*/pausar",
                        "/api/tarefas/*/finalizar"
                ).hasAnyRole("GERENTE", "DESENVOLVEDOR")
                /*.requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/gerente/**").hasRole("GERENTE")
                .requestMatchers("/api/desenvolvedor/**").hasRole("DESENVOLVEDOR")*/
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // 3. Define as regras globais de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Coloque aqui a URL exata do seu frontend (ex: Vite usa 5173, Create React App usa 3000)
        // Adicione a URL exata gerada pela Vercel (sem a barra / no final)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",           // Para desenvolvimento local (Vite)
                "http://localhost:3000",           // Para desenvolvimento local (CRA)
                "https://meu-app.vercel.app"       // <--- SEU DOMÍNIO DE PRODUÇÃO NA VERCEL
        ));
        // Métodos HTTP que a API vai aceitar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeçalhos essenciais (Authorization é obrigatório para enviar o "Bearer <token>")
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permite o tráfego de credenciais de autenticação
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica essas regras para todos os endpoints da API
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
