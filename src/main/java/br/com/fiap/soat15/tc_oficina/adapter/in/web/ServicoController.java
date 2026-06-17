package br.com.fiap.soat15.tc_oficina.adapter.in.web;

import br.com.fiap.soat15.tc_oficina.application.dto.ServicoDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviços", description = "CRUD do catálogo de serviços da oficina")
public class ServicoController {

    private final ServicoService servicoService;

    @PostMapping
    @Operation(summary = "Cadastrar novo serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou nome duplicado")
    })
    public ResponseEntity<ServicoDTO> criarServico(@Valid @RequestBody ServicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.criarServico(dto));
    }

    @GetMapping
    @Operation(summary = "Listar serviços ativos")
    public ResponseEntity<List<ServicoDTO>> listarServicos() {
        return ResponseEntity.ok(servicoService.listarServicos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    public ResponseEntity<ServicoDTO> obterServicoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.obterServicoPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço")
    public ResponseEntity<ServicoDTO> atualizarServico(@PathVariable Long id, @Valid @RequestBody ServicoDTO dto) {
        return ResponseEntity.ok(servicoService.atualizarServico(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover serviço (soft delete)")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        servicoService.deletarServico(id);
        return ResponseEntity.noContent().build();
    }
}
