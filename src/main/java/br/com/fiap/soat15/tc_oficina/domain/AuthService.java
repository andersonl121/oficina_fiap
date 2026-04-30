package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.model.LoginRequest;
import br.com.fiap.soat15.tc_oficina.domain.model.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void registro(LoginRequest request);
}
