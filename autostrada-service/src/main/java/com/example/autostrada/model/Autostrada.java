package com.example.autostrada.model;

import jakarta.persistence.*;

@Entity
@Table(name = "autostrade")
public class Autostrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autostrada")
    private Integer id;

    @Column(name = "sigla", nullable = false)
    private String sigla;

    @Column(name = "id_regione", nullable = false)
    private Integer idRegione;

    public Autostrada(String sigla, Integer idRegione) {
        this.sigla = sigla;
        this.idRegione = idRegione;
    }

    public Autostrada(Integer id, String sigla, Integer idRegione) {
        this.id = id;
        this.sigla = sigla;
        this.idRegione = idRegione;
    }

    public Autostrada(){}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSigla() { return sigla; }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Integer getIdRegione() { return idRegione; }

    public void setIdRegione(Integer idRegione) {
        this.idRegione = idRegione;
    }
}
