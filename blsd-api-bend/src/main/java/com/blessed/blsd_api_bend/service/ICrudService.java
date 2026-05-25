package com.blessed.blsd_api_bend.service;

import java.util.List;

public interface ICrudService<T> {
    List<T> listarTodos();
    T listarPorId(Long id);
    T cadastrar(T req);
    T atualizar(Long id, T req);
    void deletar(Long id);
}