package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioResponseDTO;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AcessoRepository acessoRepository;

    @Mock
    private UnicidadeService unicidadeService;

    @InjectMocks
    private FuncionarioService funcionarioService;

    @Test
    void deveCadastrarFuncionarioComDadosTratados() {
        FuncionarioRequestDTO request = new FuncionarioRequestDTO();
        request.setNome("Julia");
        request.setEmail("julia@empresa.com");
        request.setSenha("senha");
        request.setCpf("529.982.247-25");

        Acesso acesso = new Acesso();
        acesso.setNome(TiposAcessos.FUNCIONARIO);
        when(funcionarioRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(passwordEncoder.encode("senha")).thenReturn("senha-hash");
        when(acessoRepository.findByNome(TiposAcessos.FUNCIONARIO)).thenReturn(Optional.of(acesso));
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FuncionarioResponseDTO resultado = funcionarioService.cadastrar(request);

        assertEquals("Julia", resultado.getNome());
        assertEquals("julia@empresa.com", resultado.getEmail());
        verify(unicidadeService).validarEmailUnico(request.getEmail(), null);
        verify(passwordEncoder).encode("senha");
    }

    @Test
    void deveRecusarCpfDuplicado() {
        FuncionarioRequestDTO request = new FuncionarioRequestDTO();
        request.setEmail("julia@empresa.com");
        request.setCpf("529.982.247-25");
        when(funcionarioRepository.existsByCpf(request.getCpf())).thenReturn(true);

        assertThrows(FuncionarioAlreadyExistsException.class, () -> funcionarioService.cadastrar(request));
    }

    @Test
    void deveAtualizarDadosDoFuncionario() {
        Funcionario existente = new Funcionario();
        existente.setId(1L);
        FuncionarioAtualizarRequestDTO request = new FuncionarioAtualizarRequestDTO();
        request.setNome("Julia Atualizada");
        request.setEmail("julia.nova@empresa.com");
        request.setCpf("529.982.247-25");
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(funcionarioRepository.save(existente)).thenReturn(existente);

        Funcionario resultado = funcionarioService.atualizar(1L, request);

        assertEquals("Julia Atualizada", resultado.getNome());
        assertEquals("julia.nova@empresa.com", resultado.getEmail());
        verify(unicidadeService).validarEmailUnico(request.getEmail(), 1L);
    }

    @Test
    void deveListarFuncionariosPaginados() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Julia");
        when(funcionarioRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(funcionario)));

        var resultado = funcionarioService.listarTodos(PageRequest.of(0, 10));

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Julia", resultado.getContent().get(0).getNome());
    }
}
