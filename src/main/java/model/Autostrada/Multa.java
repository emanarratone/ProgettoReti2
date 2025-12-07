package model.Autostrada;

import java.time.LocalDateTime;

public class Multa {

    private Integer id;
    private String targa;
    private Double importo;
    private LocalDateTime data;
    private Boolean pagato;
    private Integer biglietto;

    public Multa(Integer id, Integer biglietto, Double importo, LocalDateTime data, String targa) {
        this.importo = importo;
        this.biglietto = biglietto;
        this.id = id;
        this.data = data;
        this.targa = targa;
        this.pagato = false;
    }

    public Multa(Integer biglietto, Double importo, LocalDateTime data, String targa) {
        this.importo = importo;
        this.biglietto = biglietto;
        this.data = data;
        this.targa = targa;
        this.pagato = false;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getPagato() {
        return pagato;
    }

    public Integer getBiglietto() {
        return biglietto;
    }

    public void setBiglietto(Integer biglietto) {
        this.biglietto = biglietto;
    }

    public String getTarga() {
        return targa;
    }

    public Double getImporto() {
        return importo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(Boolean pagato) {
        this.pagato = pagato;
    }
}
