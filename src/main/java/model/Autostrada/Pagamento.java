package model.Autostrada;

import java.time.LocalDate;

public class Pagamento {

    private String ID_transazione;
    private Biglietto biglietto;
    private Double prezzo;
    private Boolean Status;
    private LocalDate timestamp_in;

    public Pagamento(String ID_transazione, Biglietto biglietto, Double prezzo, Boolean status, LocalDate timestamp_in) {
        this.ID_transazione = ID_transazione;
        this.biglietto = biglietto;
        this.prezzo = prezzo;
        Status = status;
        this.timestamp_in = timestamp_in;
    }

    public String getID_transazione() {
        return ID_transazione;
    }

    public void setID_transazione(String ID_transazione) {
        this.ID_transazione = ID_transazione;
    }

    public Biglietto getBiglietto() {
        return biglietto;
    }

    public void setBiglietto(Biglietto biglietto) {
        this.biglietto = biglietto;
    }

    public Double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public LocalDate getTimestamp_in() {
        return timestamp_in;
    }

    public void setTimestamp_in(LocalDate timestamp_in) {
        this.timestamp_in = timestamp_in;
    }
}
