package com.blessed.blsd_api_bend.service;

import com.blessed.blsd_api_bend.exception.servico.ServicoAlreadyExistsException;
import com.blessed.blsd_api_bend.exception.servico.ServicoNotFoundException;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.entity.Servico;
import com.blessed.blsd_api_bend.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService implements ICrudService<Servico> {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }


    @Override
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    @Override
    public Servico listarPorId(Long id) {
        return servicoRepository.findById(id).orElseThrow(()-> new ServicoNotFoundException("Servico não encontrado"));
    }

    @Override
    public Servico cadastrar(Servico req) {
        if(servicoRepository.existsByNome(req.getNome())){
            throw new ServicoAlreadyExistsException("Servico já existente");
        }

        return servicoRepository.save(req);
    }

    @Override
    public Servico atualizar(Long id, Servico req) {
        return servicoRepository.findById(id).stream().
                map(p->{
                    p.setNome(p.getNome());
                    p.setPreco(p.getPreco());
                    p.setServico(p.getServico());
                    p.setQuantidade(p.getQuantidade());
                    return servicoRepository.save(p);
                }).findAny().orElseThrow(()-> new ServicoNotFoundException("Servico não encontrado") );
    }

    @Override
    public void deletar(Long id) {
        Servico servico = listarPorId(id);
        servicoRepository.delete(servico);
    }
}
