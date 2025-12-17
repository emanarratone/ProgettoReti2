package com.uniupo.corsia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "corsia")
public class Corsia {

    // Primary key: id_corsia (auto-generated)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corsia")
    private Integer idCorsia;

    @Column(name = "id_casello", nullable = false)
    private Integer casello;

    @Column(name = "num_corsia", nullable = false)
    private Integer numCorsia;

    @Enumerated(EnumType.STRING)
    @Column(name = "verso", nullable = false)
    private Verso verso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_corsia", nullable = false)
    private Tipo tipo;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed;

    public enum Tipo { NORMALE, TELEPASS, EMERGENZA }
    public enum Verso { ENTRATA, USCITA }

    public Corsia() {}

    public Corsia(Integer casello, Integer numCorsia, Verso verso, Tipo tipo) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = false;
    }

    public Corsia(Integer casello, Integer numCorsia, Verso verso, Tipo tipo, boolean isClosed) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = isClosed;
    }

    public Integer getIdCorsia() { return idCorsia; }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public Integer getCasello() {
        return casello;
    }

    public void setCasello(Integer casello) { this.casello = casello; }

    public Verso getVerso() {
        return verso;
    }

    public void setVerso(Verso verso) {
        this.verso = verso;
    }

    public Integer getNumCorsia() {
        return numCorsia;
    }

    @Override
    public String toString() {
        return "Casello "+ this.casello + "\n" +
                "ID "+ this.numCorsia + "\n" +
                "Stato: " + (this.isClosed ? "aperto" : "chiuso");
    }
}
