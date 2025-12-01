package model.Autostrada;

import java.time.LocalDateTime;
import java.time.Duration;

public class Pagamento {

    private Integer ID_transazione;
    private Biglietto biglietto;
    private Casello casello_out;
    private Double prezzo;
    private Boolean Status; //pagato = false
    private LocalDateTime timestamp_out;

    public Pagamento(Integer ID_transazione, Biglietto biglietto, Double prezzo, Boolean status, LocalDateTime timestamp_out, Casello casello_out) {
        this.ID_transazione = ID_transazione;
        this.biglietto = biglietto;
        this.prezzo = prezzo;
        this.Status = status;
        this.timestamp_out = timestamp_out;
        this.casello_out = casello_out;
        checkMulta(biglietto.getCasello_in().getLimite());
    }

    public Integer getID_transazione() {
        return ID_transazione;
    }

    public void setID_transazione(Integer ID_transazione) {
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
        return this.Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public LocalDateTime getTimestamp_out() {
        return timestamp_out;
    }

    public void setTimestamp_out(LocalDateTime timestamp_out) {
        this.timestamp_out= timestamp_out;
    }

    public void checkMulta(Integer limite){
        long durata = Duration.between(timestamp_out, biglietto.getTimestamp_in()).toMinutes();
        if(durata<limite){
            Multa multa = new Multa(11, biglietto, 10.0, LocalDateTime.now(), biglietto.getAuto().getTarga());
            //biglietto.getAuto().getMulte().add(multa);  ????
        }
    }

    public Casello getCasello_out() {
        return casello_out;
    }

    public void setCasello_out(Casello casello_out) {
        this.casello_out = casello_out;
    }
}
