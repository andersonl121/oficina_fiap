package br.com.fiap.soat15.tc_oficina.adapter.in.web;

import br.com.fiap.soat15.tc_oficina.application.dto.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.domain.validator.StringUtils;
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
@RequestMapping("/api/v1/item-estoque")
@RequiredArgsConstructor
@Tag(name = "ItemEstoque", description = "CRUD ItemEstoque")
public class ItemEstoqueController {

    private final ItemEstoqueService itemEstoqueService;

    @PostMapping
    @Operation(summary = "Criar novo Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "409", description = "Item já existe")
    })
    public ResponseEntity<ItemEstoqueDTO> adicionarItem(@Valid @RequestBody ItemEstoqueDTO dto) {
        dto.setNome(StringUtils.removerAcentos(dto.getNome()));
        return ResponseEntity.status(HttpStatus.CREATED).body(itemEstoqueService.criarItem(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter Item do estoque por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ItemEstoqueDTO> consultarItemPorId(@PathVariable Long id) {
        ItemEstoque item = itemEstoqueService.consultarItemPorId(id);
        return ResponseEntity.ok(ItemEstoqueDTO.builder()
                .id(item.getId())
                .nome(item.getNome())
                .descricao(item.getDescricao())
                .quantidadeEstoque(item.getQuantidadeEstoque())
                .precoUnitario(item.getPrecoUnitario())
                .tipo(item.getTipo())
                .ativo(item.getAtivo())
                .build());
    }

    @GetMapping("/nome/{nome}")
    @Operation(summary = "Obter Item por Nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<List<ItemEstoqueDTO>> obterItemPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(itemEstoqueService.consultarItemPorNome(nome));
    }

    @GetMapping
    @Operation(summary = "Listar todos os items do estoque")
    public ResponseEntity<List<ItemEstoqueDTO>> listarEstoque() {
        return ResponseEntity.ok(itemEstoqueService.listarTodosItems());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Item no estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ItemEstoqueDTO> atualizarItemEstoque(@PathVariable Long id,
                                                               @Valid @RequestBody ItemEstoqueDTO dto) {
        ItemEstoque item = itemEstoqueService.atualizarItem(id, dto);
        return ResponseEntity.ok(ItemEstoqueDTO.builder()
                .id(item.getId())
                .nome(item.getNome())
                .descricao(item.getDescricao())
                .quantidadeEstoque(item.getQuantidadeEstoque())
                .precoUnitario(item.getPrecoUnitario())
                .tipo(item.getTipo())
                .ativo(item.getAtivo())
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar Item do estoque (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<Void> deletarItem(@PathVariable Long id) {
        ItemEstoque item = itemEstoqueService.consultarItemPorId(id);
        itemEstoqueService.deletarItem(item);
        return ResponseEntity.noContent().build();
    }
}
