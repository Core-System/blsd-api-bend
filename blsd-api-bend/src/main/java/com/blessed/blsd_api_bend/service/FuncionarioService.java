package com.blessed.blsd_api_bend.service;

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

    public FuncionarioService(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder, FileStorageService fileStorageService, AcessoRepository acessoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.acessoRepository = acessoRepository;
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
        if (funcionarioRepository.existsByEmail(dto.getEmail())) {
            throw new FuncionarioAlreadyExistsException("Este e-mail já está em uso.");
        }

        if(funcionarioRepository.existsByCpf(dto.getCpf())){
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

    public Funcionario atualizar(Long id, Funcionario funcionario) {
        return funcionarioRepository.findById(id).map(f -> {
            f.setNome(funcionario.getNome());
            f.setEmail(funcionario.getEmail());
            String novaSenha = funcionario.getSenha();
            if (novaSenha != null && !novaSenha.isBlank()){
                f.setSenha(passwordEncoder.encode(novaSenha));
            }
            f.setUrlFoto(funcionario.getUrlFoto());
            f.setCpf(funcionario.getCpf());
            f.setEmpresa(funcionario.getEmpresa());
            f.setAcesso(funcionario.getAcesso());
            f.setConsulta(funcionario.getConsulta());
            return funcionarioRepository.save(f);
        }).orElseThrow(() -> new FuncionarioNotFoundException("Funcionario não encontrado"));
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
