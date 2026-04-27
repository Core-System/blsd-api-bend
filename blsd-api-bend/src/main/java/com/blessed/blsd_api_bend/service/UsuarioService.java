package com.blessed.blsd_api_bend.service;

import java.util.List;

public interface UsuarioService<T> {
    List<T> listarTodos();
    T listarPorId(Long id);
    T buscarPorEmail(String email);
    T cadastrar(T usuario);
    T atualizar(Long id, T usuario);
    void deletar(Long id);
}