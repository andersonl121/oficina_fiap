package br.com.fiap.soat15.tc_oficina.domain.service;

import br.com.fiap.soat15.tc_oficina.application.dto.LoginRequest;
import br.com.fiap.soat15.tc_oficina.application.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void registro(LoginRequest request);
}
