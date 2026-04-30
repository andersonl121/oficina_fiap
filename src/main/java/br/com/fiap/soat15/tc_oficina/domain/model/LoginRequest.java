package br.com.fiap.soat15.tc_oficina.domain.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
