package br.com.fiap.soat15.tc_oficina.infrastructure.repository;

import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Long> {

    @Query(value = "SELECT * FROM tb_item_estoque i WHERE LOWER(i.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND i.ativo = true", nativeQuery = true)
    List<ItemEstoque> findByAtivoAndNomeLike(@Param("nome") String nome);

    Optional<ItemEstoque> findByIdAndAtivo(Long id, boolean b);

    List<ItemEstoque> findAllByAtivo(boolean b);

    Optional<Object> findByNomeAndAtivo(@NotBlank(message = "Nome é obrigatório") String nome, boolean b);
}
