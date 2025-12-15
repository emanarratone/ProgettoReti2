package com.example.autostrada.model.dto;

public class AutostradaCreateUpdateDTO {

    private String sigla;
    private Integer idRegione;

    public Integer getIdRegione() {
        return idRegione;
    }

    public void setIdRegione(Integer idRegione) {
        this.idRegione = idRegione;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
