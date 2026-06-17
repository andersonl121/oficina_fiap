package br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository;

import br.com.fiap.soat15.tc_oficina.domain.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
}
