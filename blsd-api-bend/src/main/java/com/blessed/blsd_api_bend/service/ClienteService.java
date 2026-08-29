package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.dto.cliente.ClienteAtualizarRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.dto.cliente.ClienteResponseDTO;
import com.blessed.blsd_api_bend.dto.usuario.UsuarioMapper;
import com.blessed.blsd_api_bend.exception.cliente.ClienteAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.cliente.ClienteNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.enums.TiposAcessos;
import com.blessed.blsd_api_bend.repository.AcessoRepository;
import com.blessed.blsd_api_bend.repository.ClienteRepository;
import com.blessed.blsd_api_bend.repository.ConsultaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AcessoRepository acessoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConsultaRepository consultaRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          AcessoRepository acessoRepository,
                          PasswordEncoder passwordEncoder,
                          ConsultaRepository consultaRepository) {
        this.clienteRepository = clienteRepository;
        this.acessoRepository = acessoRepository;
        this.passwordEncoder = passwordEncoder;
        this.consultaRepository = consultaRepository;
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarTodos(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(UsuarioMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = buscarEntidadePorId(id);
        return UsuarioMapper.toResponseDTO(cliente);
    }

    private Cliente buscarEntidadePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente com ID " + id + " não encontrado."));
    }

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new ClienteAlreadyExistsException("Este e-mail já está em uso.");
        }

        if(clienteRepository.existsByTelefone(dto.getTelefone())){
            throw new ClienteAlreadyExistsException("Este telefone já está em uso.");
        }

        Cliente cliente = UsuarioMapper.of(dto);
        cliente.setSenha(passwordEncoder.encode(dto.getSenha()));

        Acesso acessoCliente = acessoRepository.findByNome(TiposAcessos.CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Acesso CLIENTE não inicializado no sistema."));

        cliente.setAcesso(acessoCliente);

        Cliente salvo = clienteRepository.save(cliente);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteAtualizarRequestDTO dto) {
        Cliente cliente = buscarEntidadePorId(id);

        if (!cliente.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (clienteRepository.existsByEmail(dto.getEmail())) {
                throw new ClienteAlreadyExistsException("Este e-mail já está sendo utilizado por outra conta.");
            }
        }

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setDataNasc(dto.getDataNasc());
        cliente.setUrlFoto(dto.getUrlFoto());

        Cliente atualizado = clienteRepository.save(cliente);
        return UsuarioMapper.toResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = buscarEntidadePorId(id);

        consultaRepository.deleteAll(consultaRepository.findByClienteId(id));
        clienteRepository.delete(cliente);
    }
}