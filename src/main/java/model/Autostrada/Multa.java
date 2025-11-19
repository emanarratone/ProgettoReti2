package model.Autostrada;

import java.time.LocalDateTime;

public class Multa {

    private String targa;
    private Double importo;
    private LocalDateTime data;
    private Boolean pagato;

    public Multa(Double importo, LocalDateTime data, String targa) {
        this.importo = importo;
        this.data = data;
        this.targa = targa;
        this.pagato = false;
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
