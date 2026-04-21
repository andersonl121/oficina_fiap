package br.com.fiap.soat15.tc_oficina.domain.impl;

import br.com.fiap.soat15.tc_oficina.domain.VeiculoService;
import br.com.fiap.soat15.tc_oficina.domain.model.VeiculoDto;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.VeiculoEntity;
import br.com.fiap.soat15.tc_oficina.infrastructure.repository.VeiculoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VeiculoServiceImpl implements VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Override
    public List<VeiculoDto> listarVeiculos() {
        return veiculoRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public VeiculoDto buscarPorId(UUID id) {
        return veiculoRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Veículo não encontrado: " + id));
    }

    @Override
    public VeiculoDto criar(VeiculoDto dto) {
        if (veiculoRepository.existsByPlaca(dto.getPlaca())) {
            throw new IllegalArgumentException("Já existe um veículo com a placa: " + dto.getPlaca());
        }
        VeiculoEntity entity = toEntity(dto);
        return toDto(veiculoRepository.save(entity));
    }

    @Override
    public VeiculoDto atualizar(UUID id, VeiculoDto dto) {
        VeiculoEntity entity = veiculoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Veículo não encontrado: " + id));
        entity.setPlaca(dto.getPlaca());
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setAno(dto.getAno());
        return toDto(veiculoRepository.save(entity));
    }

    @Override
    public void deletar(UUID id) {
        if (!veiculoRepository.existsById(id)) {
            throw new NoSuchElementException("Veículo não encontrado: " + id);
        }
        veiculoRepository.deleteById(id);
    }

    private VeiculoDto toDto(VeiculoEntity entity) {
        return VeiculoDto.builder()
                .id(entity.getId())
                .placa(entity.getPlaca())
                .marca(entity.getMarca())
                .modelo(entity.getModelo())
                .ano(entity.getAno())
                // .clienteId(entity.getCliente().getId()) // TODO: descomentar quando cliente estiver pronto
                .build();
    }

    private VeiculoEntity toEntity(VeiculoDto dto) {
        return VeiculoEntity.builder()
                .placa(dto.getPlaca())
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .ano(dto.getAno())
                .build();
    }
}
