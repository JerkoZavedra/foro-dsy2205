package cl.duoc.foro.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * ms-foro NO administra cuentas ni almacena contrasenas: reenvia las mismas
 * credenciales HTTP Basic recibidas a ms-usuarios (GET /api/auth/perfil).
 * Si son validas, el principal queda como UsuarioAutenticado(id, username,
 * rol); si el servicio de usuarios no responde, nunca se autoriza el acceso.
 * Solo apropiado para demostracion local (ver README y limitaciones).
 */
@Component
public class UsuariosRemotosProvider implements AuthenticationProvider {
    private final RestClient cliente;

    public UsuariosRemotosProvider(@Value("${foro.usuarios.url}") String url) {
        this.cliente = RestClient.builder().baseUrl(url).build();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String usuario = authentication.getName();
        String clave = String.valueOf(authentication.getCredentials());
        try {
            PerfilRemoto perfil = cliente.get().uri("/api/auth/perfil")
                .headers(h -> h.setBasicAuth(usuario, clave))
                .retrieve().body(PerfilRemoto.class);
            if (perfil == null || perfil.id() == null || perfil.rol() == null
                    || !usuario.equals(perfil.username())) {
                throw new BadCredentialsException("Perfil remoto no valido");
            }
            UsuarioAutenticado principal = new UsuarioAutenticado(perfil.id(), perfil.username(), perfil.rol());
            return UsernamePasswordAuthenticationToken.authenticated(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + perfil.rol())));
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden ex) {
            throw new BadCredentialsException("Credenciales incorrectas");
        } catch (RestClientException ex) {
            // Nunca se autoriza un acceso si ms-usuarios esta caido.
            throw new AuthenticationServiceException("Servicio de usuarios no disponible", ex);
        }
    }

    @Override
    public boolean supports(Class<?> tipo) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(tipo);
    }
}
