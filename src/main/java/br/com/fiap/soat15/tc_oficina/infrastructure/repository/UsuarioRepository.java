package br.com.fiap.soat15.tc_oficina.infrastructure.repository;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);
}
