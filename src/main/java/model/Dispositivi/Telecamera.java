package model.Dispositivi;

import model.Autostrada.Corsia.Tipo;

public class Telecamera extends Dispositivi {

    public Telecamera(Integer ID, Boolean status, Tipo tipo, Integer corsia) {
        super(ID,status,tipo, corsia);
    }

    public void getTarga(){
        // ???? GESTISCE TELEPASS? COME PRENDO LA TARGA? DOVREBBE ESSERE ATTIVATA DA UN SENSORE O RIMANE ATTIVA SEMPRE?
        // SPRECHEREBBE CPU DELL'ARDUINO, FORSE VA GESTITA CON BROKER COME TUTTI GLI ALTRI DISPOSITIVI
        // I DISPOSITIVI DOVREBBERO ESSERE UN ENTITÀ SEPARATA?
    }

}
