package com.uniupo.regione.model.dto;

public class RegioneDTO {

    private Integer id;
    private String nome;

    public RegioneDTO(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
