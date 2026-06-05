package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    @Nested
    @DisplayName("Testes do Fluxo de Cadastro")
    class CadastrarFuncionarioTest {

        @Test
        @DisplayName("Deve lançar exceção apenas se e-mail E cpf já existirem simultaneamente")
        void cadastrarFuncionarioDuplicado() {
            Funcionario func = new Funcionario();
            func.setEmail("func@empresa.com");
            func.setCpf("12345678900");
            func.setConsulta(new ArrayList<>()); // Proteção contra a propriedade List legada

            when(funcionarioRepository.existsByEmail(func.getEmail())).thenReturn(true);
            when(funcionarioRepository.existsByCpf(func.getCpf())).thenReturn(true);

            assertThrows(FuncionarioAlreadyExistsException.class, () -> {
                funcionarioService.cadastrar(func);
            });

            verify(funcionarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve cadastrar funcionário com sucesso se o e-mail existir mas o CPF for inédito")
        void deveCadastrarSeCpfForInedito() {
            Funcionario func = new Funcionario();
            func.setEmail("func@empresa.com");
            func.setCpf("441.652.820-25");
            func.setConsulta(new ArrayList<>());

            // Como a service usa '&&', se o CPF retornar false, a condição falha e o fluxo salva o objeto
            when(funcionarioRepository.existsByEmail(func.getEmail())).thenReturn(true);
            when(funcionarioRepository.existsByCpf(func.getCpf())).thenReturn(false);
            when(funcionarioRepository.save(func)).thenReturn(func);

            Funcionario resultado = funcionarioService.cadastrar(func);

            assertNotNull(resultado);
            verify(funcionarioRepository, times(1)).save(func);
        }

        @Test
        @DisplayName("Deve cadastrar funcionário com sucesso se ambos e-mail e CPF forem inéditos")
        void deveCadastrarComSucessoAbsoluto() {
            Funcionario func = new Funcionario();
            func.setEmail("novo@empresa.com");
            func.setCpf("111.222.333-44");
            func.setConsulta(new ArrayList<>());

            when(funcionarioRepository.existsByEmail(func.getEmail())).thenReturn(false);
            // O Mockito nem vai avaliar o existsByCpf devido ao curto-circuito do '&&', mas deixamos explícito se necessário
            when(funcionarioRepository.save(func)).thenReturn(func);

            Funcionario resultado = funcionarioService.cadastrar(func);

            assertNotNull(resultado);
            verify(funcionarioRepository, times(1)).save(func);
        }
    }

    @Nested
    @DisplayName("Testes de Consulta e Busca")
    class BuscarFuncionarioTest {

        @Test
        @DisplayName("Deve retornar todos os funcionários cadastrados")
        void deveListarTodos() {
            List<Funcionario> listaMock = List.of(new Funcionario(), new Funcionario());
            when(funcionarioRepository.findAll()).thenReturn(listaMock);

            List<Funcionario> resultado = funcionarioService.listarTodos();

            assertEquals(2, resultado.size());
            verify(funcionarioRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar o funcionário correto quando buscado por ID existente")
        void deveBuscarPorIdComSucesso() {
            Funcionario func = new Funcionario();
            func.setId(10L);
            func.setNome("Barbeiro Blessed");

            when(funcionarioRepository.findById(10L)).thenReturn(Optional.of(func));

            Funcionario resultado = funcionarioService.listarPorId(10L);

            assertNotNull(resultado);
            assertEquals(10L, resultado.getId());
            assertEquals("Barbeiro Blessed", resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar FuncionarioNotFoundException ao buscar ID inexistente")
        void deveLancarNotFoundPorId() {
            when(funcionarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(FuncionarioNotFoundException.class, () -> {
                funcionarioService.listarPorId(99L);
            });
        }

        @Test
        @DisplayName("Deve buscar e retornar funcionário por e-mail com sucesso")
        void deveBuscarPorEmailComSucesso() {
            Funcionario func = new Funcionario();
            func.setEmail("atendimento@blessed.com");

            when(funcionarioRepository.findByEmail("atendimento@blessed.com")).thenReturn(Optional.of(func));

            Funcionario resultado = funcionarioService.buscarPorEmail("atendimento@blessed.com");

            assertNotNull(resultado);
            assertEquals("atendimento@blessed.com", resultado.getEmail());
        }
    }

    @Nested
    @DisplayName("Testes de Atualização e Exclusão")
    class ModificarFuncionarioTest {

        @Test
        @DisplayName("Deve atualizar os dados do funcionário se o ID correspondente existir")
        void deveAtualizarComSucesso() {
            Funcionario existente = new Funcionario();
            existente.setId(1L);
            existente.setNome("Nome Velho");

            Funcionario novosDados = new Funcionario();
            novosDados.setNome("Nome Atualizado");
            novosDados.setCpf("999.999.999-99");
            novosDados.setConsulta(new ArrayList<>());

            when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(inv -> inv.getArgument(0));

            Funcionario resultado = funcionarioService.atualizar(1L, novosDados);

            assertNotNull(resultado);
            assertEquals("Nome Atualizado", resultado.getNome());
            assertEquals("999.999.999-99", resultado.getCpf());
        }

        @Test
        @DisplayName("Deve deletar funcionário com sucesso a partir de um ID válido")
        void deveDeletarComSucesso() {
            Funcionario func = new Funcionario();
            func.setId(2L);

            when(funcionarioRepository.findById(2L)).thenReturn(Optional.of(func));

            assertDoesNotThrow(() -> funcionarioService.deletar(2L));

            verify(funcionarioRepository, times(1)).delete(func);
        }
    }
}