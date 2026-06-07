package com.blessed.blsd_api_bend.dto.avaliacao;

public class AvaliacaoDTO {

    private Integer nota;
    private String descricao;

    public Integer getNota(){
        return nota;
    }

    public String getDescricao(){
        return descricao;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}