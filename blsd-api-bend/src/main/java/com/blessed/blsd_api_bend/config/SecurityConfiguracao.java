package com.blessed.blsd_api_bend.config;

import com.blessed.blsd_api_bend.service    .AutenticacaoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguracao {

    private final AutenticacaoService autenticacaoService;
    private final AutenticacaoEntryPoint autenticacaoJwtEntryPoint;
    private final AutenticacaoFilter autenticacaoFilter;

    String[] ROTAS_SWAGGER_E_DOCUMENTACAO = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",
            "/v3/api-docs/**",
            "/actuator/*"
    };

    String[] ROTAS_PUBLICAS = {
            "/api/public/**",
            "/api/public/authenticate",
            "/usuarios/login/**",
            "/error/**",
            "/h2-console/**",
            "/usuarios/link-agendamento/**",
            "/funcionario/**"
    };

    String[] ROTAS_CLIENTES = {
            "/cliente",
            "/cliente/**"
    };

    String[] ROTAS_FUNCIONARIOS_E_GESTORES = {
            "/produto",
            "/produto/**",
            "/consulta/**"
    };

    String[] ROTAS_EXCLUSIVAS_GESTORES = {
            "/movimentacao",
            "/movimentacao/**"
    };

    public SecurityConfiguracao(AutenticacaoService autenticacaoService,
                                AutenticacaoEntryPoint autenticacaoJwtEntryPoint,
                                AutenticacaoFilter autenticacaoFilter) {
        this.autenticacaoService = autenticacaoService;
        this.autenticacaoJwtEntryPoint = autenticacaoJwtEntryPoint;
        this.autenticacaoFilter = autenticacaoFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:;")
                        )
                )
                .cors(Customizer.withDefaults())
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(ROTAS_PUBLICAS).permitAll()
                        .requestMatchers(ROTAS_SWAGGER_E_DOCUMENTACAO).permitAll()
                        .requestMatchers(ROTAS_CLIENTES).permitAll()
                        .requestMatchers(ROTAS_FUNCIONARIOS_E_GESTORES).permitAll()
                        .requestMatchers(ROTAS_EXCLUSIVAS_GESTORES).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handling -> handling.authenticationEntryPoint(autenticacaoJwtEntryPoint))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(autenticacaoFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(
                new AutenticacaoProvider(autenticacaoService, passwordEncoder())
        );
        return authenticationManagerBuilder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(List.of("http://localhost:5173"));
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("*"));
        configuracao.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuracao);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}