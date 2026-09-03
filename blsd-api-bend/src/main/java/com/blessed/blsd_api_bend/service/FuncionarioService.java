package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioResponseDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioMapper;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.funcionario.FuncionarioNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.repository.FuncionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final AcessoRepository acessoRepository;
    private final UnicidadeService unicidadeService;
    public FuncionarioService(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder, FileStorageService fileStorageService, AcessoRepository acessoRepository, UnicidadeService unicidadeService) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.acessoRepository = acessoRepository;
        this.unicidadeService = unicidadeService;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario listarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new FuncionarioNotFoundException("Funcionario não encontrado"));
    }

    public Funcionario buscarPorEmail(String email) {
        return funcionarioRepository.findByEmail(email)
                .orElseThrow(() -> new FuncionarioNotFoundException("Funcionario não encontrado"));
    }

    @Transactional
    public FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO dto) {
        unicidadeService.validarEmailUnico(dto.getEmail(), null);

        if (funcionarioRepository.existsByCpf(dto.getCpf())) {
            throw new FuncionarioAlreadyExistsException("Este cpf já está em uso.");
        }

        Funcionario funcionario = UsuarioMapper.of(dto);
        funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Acesso acessoCliente = acessoRepository.findByNome(TiposAcessos.FUNCIONARIO)
                .orElseThrow(() -> new IllegalStateException("Acesso FUNCIONARIO não inicializado no sistema."));

        funcionario.setAcesso(acessoCliente);

        Funcionario salvo = funcionarioRepository.save(funcionario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    @Transactional
    public Funcionario atualizar(Long id, FuncionarioAtualizarRequestDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new FuncionarioNotFoundException("Funcionário não encontrado"));

        unicidadeService.validarEmailUnico(dto.getEmail(), id);

        if (dto.getCpf() != null && !dto.getCpf().equals(funcionario.getCpf()) && funcionarioRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já está em uso por outro funcionário.");
        }

        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setCpf(dto.getCpf());
        funcionario.setUrlFoto(dto.getUrlFoto());

        return funcionarioRepository.save(funcionario);
    }

    public void deletar(Long id) {
        Funcionario funcionario = listarPorId(id);
        funcionarioRepository.delete(funcionario);
    }

    public void atualizarFotoPerfil(Long funcionarioId, MultipartFile file) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
        String caminhoRelativo = fileStorageService.armazenarArquivo(file);
        funcionario.setUrlFoto("/uploads/" + caminhoRelativo);
        funcionarioRepository.save(funcionario);
    }
}
