package model.Autostrada;

import java.time.LocalDateTime;

public class Multa {

    private final Integer id;
    private String targa;
    private Double importo;
    private LocalDateTime data;
    private Boolean pagato;
    private Biglietto biglietto;

    public Multa(Integer id, Biglietto biglietto, Double importo, LocalDateTime data, String targa) {
        this.importo = importo;
        this.biglietto = biglietto;
        this.id = id;
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

    public Biglietto getBiglietto() {
        return biglietto;
    }

    public void setBiglietto(Biglietto biglietto) {
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
