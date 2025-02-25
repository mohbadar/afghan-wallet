package af.gov.anar.corona.infrastructure.config;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.representations.AccessToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Starting from Spring Boot Keycloak Adapter 7.0.0, we are required to
 * explicitly define a KeycloakSpringBootConfigResolver bean to make Spring Boot
 * resolve the Keycloak configuration from application.properties (or
 * application.yml) correctly. It must be defined in a @Configuration class.
 *
 * This class is normally not necessary. As of {@code KEYCLOAK-11282}, declaring
 * the {@link KeycloakSpringBootConfigResolver} directly in your
 * {@link Configuration} class that extends from
 * {@link KeycloakWebSecurityConfigurerAdapter} will cause the Spring Boot
 * application context not to load.
 *
 * Additional note: As of Keycloak starter version 8.0.0 using this helper there
 * is also no need to override
 * {@code spring.main.allow-bean-definition-overriding} property to
 * {@code true}.
 *
 * https://issues.redhat.com/browse/KEYCLOAK-11282
 */
@Configuration
public class KeycloakConfig {

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessToken getAccessToken() {
        return ((KeycloakPrincipal) getRequest().getUserPrincipal()).getKeycloakSecurityContext().getToken();
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public KeycloakSecurityContext getKeycloakSecurityContext() {
        return ((KeycloakPrincipal) getRequest().getUserPrincipal()).getKeycloakSecurityContext();
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

}
