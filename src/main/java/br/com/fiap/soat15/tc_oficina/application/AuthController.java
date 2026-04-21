package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.domain.model.LoginRequest;
import br.com.fiap.soat15.tc_oficina.domain.model.LoginResponse;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.UsuarioEntity;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.UsuarioRepository;
import br.com.fiap.soat15.tc_oficina.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Autenticação", description = "Login e registro de usuários administrativos")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login e obter token JWT")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtService.gerarToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar novo usuário administrativo")
    public ResponseEntity<Void> registro(@RequestBody LoginRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Usuário já existe: " + request.getUsername());
        }
        usuarioRepository.save(UsuarioEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build());
        return ResponseEntity.ok().build();
    }
}
