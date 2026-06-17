package br.com.fiap.soat15.tc_oficina.application.usecase;

import br.com.fiap.soat15.tc_oficina.application.dto.LoginRequest;
import br.com.fiap.soat15.tc_oficina.application.dto.LoginResponse;
import br.com.fiap.soat15.tc_oficina.config.security.JwtService;
import br.com.fiap.soat15.tc_oficina.domain.entity.Usuario;
import br.com.fiap.soat15.tc_oficina.domain.service.AuthService;
import br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository.UsuarioRepository;
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
