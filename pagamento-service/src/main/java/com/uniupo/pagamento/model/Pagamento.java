package com.uniupo.pagamento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transazione")
    private Integer idTransazione;
    
    @Column(name = "id_biglietto")
    private Integer idBiglietto;
    
    @Column(name = "casello_out")
    private Integer caselloOut;
    
    @Column(name = "prezzo")
    private Double prezzo;
    
    @Column(name = "pagato")
    private Boolean pagato;
    
    @Column(name = "timestamp_out")
    private LocalDateTime timestampOut;

    public Pagamento() {}

    public Pagamento(Integer idBiglietto, Double prezzo, Boolean pagato, LocalDateTime timestampOut, Integer caselloOut) {
        this.idBiglietto = idBiglietto;
        this.prezzo = prezzo;
        this.pagato = pagato;
        this.timestampOut = timestampOut;
        this.caselloOut = caselloOut;
    }

    public Pagamento(Integer idTransazione, Integer idBiglietto, Double prezzo, Boolean pagato, 
                     LocalDateTime timestampOut, Integer caselloOut) {
        this.idTransazione = idTransazione;
        this.idBiglietto = idBiglietto;
        this.prezzo = prezzo;
        this.pagato = pagato;
        this.timestampOut = timestampOut;
        this.caselloOut = caselloOut;
    }

    // Getters and Setters
    public Integer getIdTransazione() { return idTransazione; }
    public void setIdTransazione(Integer idTransazione) { this.idTransazione = idTransazione; }

    public Integer getIdBiglietto() { return idBiglietto; }
    public void setIdBiglietto(Integer idBiglietto) { this.idBiglietto = idBiglietto; }

    public Integer getCaselloOut() { return caselloOut; }
    public void setCaselloOut(Integer caselloOut) { this.caselloOut = caselloOut; }

    public Double getPrezzo() { return prezzo; }
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }

    public Boolean getPagato() { return pagato; }
    public void setPagato(Boolean pagato) { this.pagato = pagato; }

    public LocalDateTime getTimestampOut() { return timestampOut; }
    public void setTimestampOut(LocalDateTime timestampOut) { this.timestampOut = timestampOut; }
}
