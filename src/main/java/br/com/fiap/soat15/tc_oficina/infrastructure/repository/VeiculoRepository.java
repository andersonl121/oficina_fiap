package br.com.fiap.soat15.tc_oficina.infrastructure.repository;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Optional<Veiculo> findByPlaca(String placa);

    boolean existsByPlaca(String placa);
}
