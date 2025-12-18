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

    @Column(name = "tipo_dispositivo", nullable = false)
    protected String tipoDispositivo;

    public Dispositivo() {}

    public Dispositivo(Boolean status, Integer corsia, Integer casello, String tipoDispositivo) {
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
        this.tipoDispositivo = tipoDispositivo;
    }

    public Dispositivo(Integer ID, Boolean status, Integer corsia, Integer casello, String tipoDispositivo) {
        this.ID = ID;
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
        this.tipoDispositivo = tipoDispositivo;
    }

    public String getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(String tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
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
