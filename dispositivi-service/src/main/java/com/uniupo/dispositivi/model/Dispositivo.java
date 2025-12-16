package com.uniupo.dispositivi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dispositivo")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo")
    protected Integer ID;

    @Column(name = "num_corsia", nullable = false)
    private Integer corsia;

    @Column(name = "id_casello", nullable = false)
    private Integer casello;

    @Column(name = "stato", nullable = false)
    protected Boolean status;

    public Dispositivo() {}

    public Dispositivo(Integer ID, Boolean status, Integer corsia, Integer casello) {
        this.ID = ID;
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
    }

    public Dispositivo(Boolean status, Integer corsia, Integer casello) {
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
    }

    public Integer getID() { 
        return ID; 
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getCorsia() {
        return corsia;
    }

    public void setCorsia(Integer corsia) {
        this.corsia = corsia;
    }

    public Integer getCasello() { 
        return casello; 
    }

    public void setCasello(Integer casello) {
        this.casello = casello;
    }

    public String getStatus() {
        return (status) ? "ATTIVO" : "INATTIVO";
    }

    public Boolean getStatusBoolean() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
