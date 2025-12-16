package com.uniupo.casello.model.dto;

public class CaselloDTO {

    private Integer idCasello;
    private String sigla;
    private Integer idAutostrada;
    private boolean closed;
    private Integer limite;

    public CaselloDTO(Integer idCasello, String sigla, Integer idAutostrada, boolean closed, Integer limite) {
        this.idCasello = idCasello;
        this.sigla = sigla;
        this.idAutostrada = idAutostrada;
        this.closed = closed;
        this.limite = limite;
    }

    public Integer getIdCasello() { return idCasello; }
    public void setIdCasello(Integer idCasello) { this.idCasello = idCasello; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }

    public Integer getIdAutostrada() { return idAutostrada; }
    public void setAutostrada(Integer idAutostrada) { this.idAutostrada = idAutostrada; }

    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }

    public Integer getLimite() { return limite; }
    public void setLimite(Integer limite) { this.limite = limite; }
}
