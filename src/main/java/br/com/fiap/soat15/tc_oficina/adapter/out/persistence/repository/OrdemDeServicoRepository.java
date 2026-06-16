package br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository;

import br.com.fiap.soat15.tc_oficina.domain.entity.OrdemDeServico;
import br.com.fiap.soat15.tc_oficina.domain.entity.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServico, Long> {

    List<OrdemDeServico> findByVeiculoClienteId(Long clienteId);

    boolean existsByNumero(String numero);

    @Query("SELECT os FROM OrdemDeServico os WHERE os.dataInicioExecucao BETWEEN :dataInicial AND :dataFinal AND os.status = :status")
    List<OrdemDeServico> findByDataExecucaoBetweenAndStatusEquals(
            LocalDateTime dataInicial, LocalDateTime dataFinal, StatusOS status);
}
