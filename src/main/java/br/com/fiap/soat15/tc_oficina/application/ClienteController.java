package br.com.fiap.soat15.tc_oficina.application;

import br.com.fiap.soat15.tc_oficina.domain.model.ClienteDTO;
import br.com.fiap.soat15.tc_oficina.domain.service.ClienteService;
import br.com.fiap.soat15.tc_oficina.infrastructure.entity.Cliente;
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

@RestController
@RequestMapping("/api/v1/cliente")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "CRUD clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Criar novo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "409", description = "Cliente já existe")
    })
    public ResponseEntity<ClienteDTO> criarCliente(@Valid @RequestBody ClienteDTO dto) {
        try {
            Cliente cliente = Cliente.builder()
                    .nome(dto.getNome())
                    .cpfCnpj(dto.getCpfCnpj())
                    .email(dto.getEmail())
                    .telefone(dto.getTelefone())
                    .endereco(dto.getEndereco())
                    .ativo(true)
                    .build();

            Cliente clienteCriado = clienteService.criarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(clienteCriado));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteDTO> obterClientePorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.obterClientePorId(id);
            if (cliente == null) {
                throw new BusinessException("Cliente com ID " + id + " não encontrado");
            }
            return ResponseEntity.ok(convertToDTO(cliente));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping("/cpfcnpj/{cpfCnpj}")
    @Operation(summary = "Obter cliente por CPF/CNPJ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteDTO> obterClientePorCpfCnpj(@PathVariable String cpfCnpj) {
        try {
            Cliente cliente = clienteService.obterClientePorCpfCnpj(cpfCnpj);
            if (cliente == null) {
                throw new BusinessException("Cliente com CPF/CNPJ " + cpfCnpj + " não encontrado");
            }
            return ResponseEntity.ok(convertToDTO(cliente));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        try {
            List<Cliente> clientes = clienteService.listarClientes();
            List<ClienteDTO> dtos = clientes.stream().map(this::convertToDTO).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        try {
            Cliente clienteExistente = clienteService.obterClientePorId(id);
            if (clienteExistente == null) {
                throw new BusinessException("Cliente com ID " + id + " não encontrado");
            }

            Cliente clienteAtualizado = Cliente.builder()
                    .nome(dto.getNome())
                    .email(dto.getEmail())
                    .telefone(dto.getTelefone())
                    .endereco(dto.getEndereco())
                    .build();

            Cliente cliente = clienteService.atualizarCliente(id, clienteAtualizado);
            return ResponseEntity.ok(convertToDTO(cliente));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        try {
            Cliente clienteExistente = clienteService.obterClientePorId(id);
            if (clienteExistente == null) {
                throw new BusinessException("Cliente com ID " + id + " não encontrado");
            }
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpfCnpj(cliente.getCpfCnpj())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .endereco(cliente.getEndereco())
                .ativo(cliente.getAtivo())
                .build();
    }
}

