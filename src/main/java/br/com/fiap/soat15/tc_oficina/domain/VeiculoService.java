package br.com.fiap.soat15.tc_oficina.domain;

import br.com.fiap.soat15.tc_oficina.domain.model.VeiculoDto;

import java.util.List;
import java.util.UUID;

public interface VeiculoService {

    List<VeiculoDto> listarVeiculos();

    VeiculoDto buscarPorId(UUID id);

    VeiculoDto criar(VeiculoDto dto);

    VeiculoDto atualizar(UUID id, VeiculoDto dto);

    void deletar(UUID id);
}
