package com.uniupo.multa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "multa")
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_multa")
    private Integer id;
    
    @Column(name = "targa")
    private String targa;
    
    @Column(name = "importo")
    private Double importo;
    
    @Column(name = "pagato")
    private Boolean pagato;
    
    @Column(name = "id_biglietto")
    private Integer idBiglietto;

    public Multa() {}

    public Multa(Integer idBiglietto, Double importo, String targa) {
        this.idBiglietto = idBiglietto;
        this.importo = importo;
        this.targa = targa;
        this.pagato = false;
    }

    public Multa(Integer id, Integer idBiglietto, Double importo, String targa) {
        this.id = id;
        this.idBiglietto = idBiglietto;
        this.importo = importo;
        this.targa = targa;
        this.pagato = false;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTarga() { return targa; }
    public void setTarga(String targa) { this.targa = targa; }

    public Double getImporto() { return importo; }
    public void setImporto(Double importo) { this.importo = importo; }

    public Boolean getPagato() { return pagato; }
    public void setPagato(Boolean pagato) { this.pagato = pagato; }

    public Integer getIdBiglietto() { return idBiglietto; }
    public void setIdBiglietto(Integer idBiglietto) { this.idBiglietto = idBiglietto; }
}
