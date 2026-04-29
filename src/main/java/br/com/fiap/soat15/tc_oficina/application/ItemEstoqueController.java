package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.domain.model.ItemEstoqueDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ItemEstoqueService;
import br.com.fiap.soat15.tc_oficina.domain.validator.StringUtils;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.ItemEstoque;
import br.com.fiap.soat15.tc_oficina.infrastructure.exception.BusinessException;
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
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/item-estoque")
@RequiredArgsConstructor
@Tag(name = "ItemEstoque", description = "Crud ItemEstoque")
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
        try {
            dto.setNome(StringUtils.removerAcentos(dto.getNome()));
            return ResponseEntity.status(HttpStatus.CREATED).body(itemEstoqueService.criarItem(dto));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter Item do estoque por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ItemEstoqueDTO> consultarItemPorId(@PathVariable Long id) {
        try {
            ItemEstoque itemEstoque = itemEstoqueService.consultarItemPorId(id);
            if (itemEstoque == null) {
                throw new BusinessException("item com ID " + id + " não encontrado");
            }
            return ResponseEntity.ok(itemEstoque.convertToDTO());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }


    @GetMapping("/nome/{nome}")
    @Operation(summary = "Obter Item por Nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<List<ItemEstoqueDTO>> obterItemPorNome(@PathVariable String nome) {
        try {

            List<ItemEstoqueDTO> items = itemEstoqueService.consultarItemPorNome(nome);

            if (items.isEmpty())
                throw new BusinessException("Não foram encontrados itens com o nome: " + nome);

            return ResponseEntity.ok(items);

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os items do estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque listado com sucesso")
    })
    public ResponseEntity<List<ItemEstoqueDTO>> listarEstoque() {
        try {
            return ResponseEntity.ok(itemEstoqueService.listarTodosItems());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Item no estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "400", description = "Item não encontrado")
    })
    public ResponseEntity<ItemEstoqueDTO> atualizarItemEstoque(@PathVariable Long id,
                                                               @Valid @RequestBody ItemEstoqueDTO dto)
    {
        try {
            ItemEstoque ItemExistente = itemEstoqueService.consultarItemPorId(id);
            if (ItemExistente == null) {
                throw new NoSuchElementException("Item com ID " + id + " não encontrado");
            }

            return ResponseEntity.ok(itemEstoqueService.atualizarItem(id, dto).convertToDTO());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar Item do estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Item não encontrado")
    })
    public ResponseEntity<Void> deletarItem(@PathVariable Long id) {
        try {
            ItemEstoque itemEstoque = itemEstoqueService.consultarItemPorId(id);
            if(itemEstoque == null)
                throw new NoSuchElementException("Item não encontrado");

            itemEstoqueService.deletarItem(itemEstoque);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
