package model.Autostrada;

import java.time.LocalDateTime;
import java.time.Duration;

public class Pagamento {

    private Integer ID_transazione;
    private Integer biglietto;
    private Integer casello_out;
    private Double prezzo;
    private Boolean Status; //pagato = false
    private LocalDateTime timestamp_out;

    public Pagamento(Integer ID_transazione, Integer biglietto, Double prezzo, Boolean status, LocalDateTime timestamp_out, Integer casello_out) {
        this.ID_transazione = ID_transazione;
        this.biglietto = biglietto;
        this.prezzo = prezzo;
        this.Status = status;
        this.timestamp_out = timestamp_out;
        this.casello_out = casello_out;
        //checkMulta(biglietto.getCasello_in().getLimite());
    }

    public Pagamento(Integer biglietto, Double prezzo, Boolean status, LocalDateTime timestamp_out, Integer casello_out) {
        this.biglietto = biglietto;
        this.prezzo = prezzo;
        this.Status = status;
        this.timestamp_out = timestamp_out;
        this.casello_out = casello_out;
        //checkMulta(biglietto.getCasello_in().getLimite());
    }

    public Integer getID_transazione() {
        return ID_transazione;
    }

    public void setID_transazione(Integer ID_transazione) {
        this.ID_transazione = ID_transazione;
    }

    public Integer getBiglietto() {
        return biglietto;
    }

    public void setBiglietto(Integer biglietto) {
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

    /*      QUESTO METODO È DA ELIMINARE (?)
    public void checkMulta(Integer limite){
        long durata = Duration.between(timestamp_out, biglietto.getTimestamp_in()).toMinutes();
        if(durata<limite){
            Multa multa = new Multa(11, biglietto.getID_biglietto(), 10.0, LocalDateTime.now(), biglietto.getAuto());
            //biglietto.getAuto().getMulte().add(multa);  ????
        }
    }

     */

    public Integer getCasello_out() {
        return casello_out;
    }

    public void setCasello_out(Integer casello_out) {
        this.casello_out = casello_out;
    }
}
