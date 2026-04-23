package br.com.fiap.soat15.tc_oficina.infrastructure.repository;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    boolean existsByCpfCnpj(String cpfCnpj);
}

//Identificacao do cliente por CPF/CNPJ;