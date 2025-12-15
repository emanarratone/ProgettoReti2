package com.example.autostrada.model.dto;

public class AutostradaDTO {

    private Integer id;
    private final String sigla;
    private final Integer idRegione;

    public AutostradaDTO(Integer id, String sigla, Integer idRegione) {
        this.id = id;
        this.sigla = sigla;
        this.idRegione = idRegione;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSigla() { return sigla; }

    public Integer getIdRegione() { return idRegione; }
}
