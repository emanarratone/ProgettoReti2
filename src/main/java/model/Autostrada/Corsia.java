package model.Autostrada;


import model.Dispositivi.Dispositivi;

import java.util.ArrayList;
import java.util.List;

public class Corsia {

    private ArrayList<Dispositivi> dispositivi;
    private Dispositivi.Tipo tipo;
    private Boolean isClosed; //

    public Corsia(ArrayList<Dispositivi> dispositivi, Dispositivi.Tipo tipo) {
        // se ti interessa copiare la lista passata:
        this.dispositivi = dispositivi;
        this.tipo = tipo;
        this.isClosed = false;
    }

    // getter che restituisce una copia "read-only" se vuoi evitare modifiche esterne
    public ArrayList<Dispositivi> getDispositivi() {
        return dispositivi;
    }

    public Dispositivi.Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Dispositivi.Tipo tipo) {
        this.tipo = tipo;
    }

    // aggiunge un dispositivo
    public void aggiungiDispositivo(Dispositivi d) {
        dispositivi.add(d);
    }

    // rimuove un dispositivo (per equals)
    public boolean rimuoviDispositivo(Dispositivi d) {
        return dispositivi.remove(d);
    }

    // rimuove per indice
    public Dispositivi rimuoviDispositivo(int index) {
        return dispositivi.remove(index);
    }

    // restituisce un dispositivo per indice
    public Dispositivi getDispositivo(int index) {
        return dispositivi.get(index);
    }

    // numero di dispositivi
    public int getNumeroDispositivi() {
        return dispositivi.size();
    }

    // svuota la lista
    public void svuotaDispositivi() {
        dispositivi.clear();
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    @Override
    public String toString() {
        return "Casello di"+ this.tipo.toString() + "\n" +
                "Stato: " + (this.isClosed ? "aperto" : "chiuso");
    }

}
