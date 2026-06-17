package br.com.fiap.soat15.tc_oficina.adapter.out.persistence.repository;

import br.com.fiap.soat15.tc_oficina.domain.entity.ItemOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemOSRepository extends JpaRepository<ItemOS, Long> {
}
