package cl.duoc.foro.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Lectura (GET) publica para categorias, temas y comentarios.
 * Publicar/comentar requiere cualquier usuario autenticado (ADMIN o USUARIO);
 * modificar o eliminar contenido propio lo valida el Service comparando el
 * autor con la identidad autenticada (ver TemaService/ComentarioService).
 * No existen rutas de moderacion ni de baneo.
 */
@Configuration
public class SeguridadConfig {

    @Bean
    SecurityFilterChain filtros(HttpSecurity http, AuthenticationProvider proveedor) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(proveedor)
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/estado").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/temas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/comentarios/**").permitAll()
                // Unico catch-all: cualquier otra combinacion metodo+ruta de este
                // microservicio (POST/PUT/DELETE sobre /api/temas o /api/comentarios)
                // exige autenticacion; no hay rutas publicas fuera de las anteriores.
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
