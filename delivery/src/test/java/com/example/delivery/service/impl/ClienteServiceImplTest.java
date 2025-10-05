package com.example.delivery.service.impl;

import com.example.delivery.dto.ClienteDTO;
import com.example.delivery.dto.ClienteInputDTO;
import com.example.delivery.entity.Cliente;
import com.example.delivery.mapper.ClienteMapper;
import com.example.delivery.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a classe ClienteServiceImpl.
 * Usamos Mockito para simular (mockar) as dependências (Repository e Mapper)
 * e testar a lógica de negócio da camada de serviço de forma isolada.
 */
@ExtendWith(MockitoExtension.class) // Habilita o uso de anotações do Mockito como @Mock e @InjectMocks
class ClienteServiceImplTest {

    // @Mock: Cria um objeto falso (mock) para a dependência.
    // Não usaremos a implementação real, mas sim uma simulação que controlamos.
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    // @InjectMocks: Cria uma instância real da classe que queremos testar (ClienteServiceImpl)
    // e injeta automaticamente os mocks criados com @Mock nela.
    @InjectMocks
    private ClienteServiceImpl clienteService;

    // Variáveis de apoio para os testes
    private Cliente cliente;
    private ClienteDTO clienteDTO;
    private ClienteInputDTO clienteInputDTO;

    // @BeforeEach: Este método é executado antes de cada teste (@Test).
    // É útil para configurar objetos que são usados em múltiplos testes.
    @BeforeEach
    void setUp() {
        // Configura um conjunto de dados padrão para os testes
        clienteInputDTO = new ClienteInputDTO("João Silva", "joao.silva@email.com", "11999998888");
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao.silva@email.com");

        clienteDTO = new ClienteDTO(1L, "João Silva", "joao.silva@email.com", "11999998888");
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void deveCriarClienteComSucesso() {
        // --- ARRANGE (Arranjar / Configurar) ---
        // 1. Configura o comportamento dos mocks.
        // Quando o repositório procurar por email, deve retornar vazio (indicando que não existe).
        when(clienteRepository.findByEmail(clienteInputDTO.email())).thenReturn(Optional.empty());
        // Quando o mapper converter o DTO de entrada para entidade, deve retornar nosso objeto 'cliente'.
        when(clienteMapper.toEntity(clienteInputDTO)).thenReturn(cliente);
        // Quando o repositório salvar QUALQUER entidade Cliente, deve retornar nosso objeto 'cliente' salvo.
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        // Quando o mapper converter a entidade para o DTO de saída, deve retornar nosso 'clienteDTO'.
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        // --- ACT (Agir / Executar) ---
        // 2. Chama o método que está sendo testado.
        ClienteDTO resultado = clienteService.criar(clienteInputDTO);

        // --- ASSERT (Verificar / Afirmar) ---
        // 3. Verifica se o resultado é o esperado.
        assertNotNull(resultado); // O resultado não deve ser nulo.
        assertEquals(clienteDTO.id(), resultado.id()); // O ID deve ser o mesmo.
        assertEquals(clienteDTO.nome(), resultado.nome()); // O nome deve ser o mesmo.
        assertEquals("joao.silva@email.com", resultado.email()); // O email deve ser o mesmo.

        // 4. Verifica se os mocks foram chamados como esperado.
        // Garante que o método 'save' do repositório foi chamado exatamente 1 vez.
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar cliente com email duplicado")
    void deveLancarExcecaoAoCriarClienteComEmailDuplicado() {
        // --- ARRANGE ---
        // Configura o mock para que, ao procurar pelo email, ele ENCONTRE um cliente existente.
        when(clienteRepository.findByEmail(clienteInputDTO.email())).thenReturn(Optional.of(cliente));

        // --- ACT & ASSERT ---
        // Verifica se a execução do método 'criar' lança a exceção esperada.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.criar(clienteInputDTO);
        });

        // Verifica se a mensagem da exceção é a correta.
        assertEquals("E-mail já cadastrado.", exception.getMessage());

        // Garante que o método 'save' NUNCA foi chamado, pois a lógica deve parar antes.
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve buscar um cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // --- ARRANGE ---
        // Configura o mock para retornar nosso cliente quando 'findById' for chamado com o ID 1.
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteDTO);

        // --- ACT ---
        ClienteDTO resultado = clienteService.buscarPorId(1L);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente com ID inexistente")
    void deveLancarExcecaoAoBuscarClienteComIdInexistente() {
        // --- ARRANGE ---
        // Configura o mock para retornar um Optional vazio, simulando um cliente não encontrado.
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        // Verifica se a exceção EntityNotFoundException é lançada.
        assertThrows(EntityNotFoundException.class, () -> {
            clienteService.buscarPorId(99L);
        });
    }
}
