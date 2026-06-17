package br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository;

import br.com.fiap.soat15.tc_oficina.domain.entity.ItemEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Long> {

    @Query(value = "SELECT * FROM tb_item_estoque i WHERE LOWER(i.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND i.ativo = true", nativeQuery = true)
    List<ItemEstoque> findByAtivoAndNomeLike(@Param("nome") String nome);

    Optional<ItemEstoque> findByIdAndAtivo(Long id, boolean ativo);

    List<ItemEstoque> findAllByAtivo(boolean ativo);

    Optional<ItemEstoque> findByNomeAndAtivo(String nome, boolean ativo);
}
