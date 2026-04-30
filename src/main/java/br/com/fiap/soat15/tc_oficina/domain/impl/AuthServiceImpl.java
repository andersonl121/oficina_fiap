package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.AuthService;
import br.com.fiap.soat15.tc_oficina.domain.model.LoginRequest;
import br.com.fiap.soat15.tc_oficina.domain.model.LoginResponse;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Usuario;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.UsuarioRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtService.gerarToken(request.getUsername());
        return new LoginResponse(token);
    }

    @Override
    public void registro(LoginRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Usuário já existe: " + request.getUsername());
        }
        usuarioRepository.save(Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build());
    }
}
