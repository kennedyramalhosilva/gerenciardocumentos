package com.sistema.gerenciardocumentos.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Value("${api.key}")
    private String apiKey;

    //apenas uma vez por requisição
    //roda toda requisicao pra filtrar se libera ou não o acesso a api
    public class ApiKeyFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {


            String path = request.getRequestURI();

            // Liberar acesso para Swagger e afins sem exigir API Key
            if (path.startsWith("/swagger-ui")
                    || path.startsWith("/v3/api-docs")
                    || path.equals("/swagger-ui.html")
                    || path.equals("/favicon.ico")) {
                filterChain.doFilter(request, response);
                return;  // pula validação da API Key
            }
            //pega a chave do header e ve se bate com a configurada no arquivo propertiess
            String key = request.getHeader("X-API-KEY");

            if (key == null || !key.equals(apiKey)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "API Key inválida");
                return;
            }

            //aqui serve só para o spring liberar o acesso para um usuario ficticio
            //senao fica dando erro 403
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    "apiKeyUser", null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new ApiKeyFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
