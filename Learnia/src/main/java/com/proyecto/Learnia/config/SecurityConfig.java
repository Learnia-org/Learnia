package com.proyecto.Learnia.config;

import com.proyecto.Learnia.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/img/**", "/uploads/**", "/materias/**", "/api/chatbot/**"  ).permitAll()
                        .requestMatchers("/menu-admin", "/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("correoUsuario")
                        .passwordParameter("contrasenaUsuario")
                        .successHandler((request, response, authentication) -> {
                            String email = authentication.getName();
                            usuarioRepository.findByCorreoUsuario(email).ifPresent(u -> {
                                u.setEnLinea(true);
                                u.setUltimoAcceso(LocalDateTime.now());
                                usuarioRepository.save(u);
                            });
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                            try {
                                if (isAdmin) {
                                    response.sendRedirect("/menu-admin");
                                } else {
                                    response.sendRedirect("/menu");
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            if (exception instanceof DisabledException) {
                                response.sendRedirect("/login?bloqueado");
                            } else {
                                response.sendRedirect("/login?error");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                String email = authentication.getName();
                                usuarioRepository.findByCorreoUsuario(email).ifPresent(u -> {
                                    u.setEnLinea(false);
                                    usuarioRepository.save(u);
                                });
                            }
                        })
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            var usuario = usuarioRepository.findByCorreoUsuario(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

            return User.builder()
                    .username(usuario.getCorreoUsuario())
                    .password(usuario.getContrasenaUsuario())
                    .roles(usuario.getRolUsuario().name())
                    .disabled(usuario.isBloqueado())
                    .build();
        };
    }
}
