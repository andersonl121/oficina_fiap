package br.com.fiap.soat15.tc_oficina.infrastructure.repository;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.OrdemDeServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServico, Long> {

    List<OrdemDeServico> findByVeiculoClienteId(Long clienteId);

    boolean existsByNumero(String numero);
}
