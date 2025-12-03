package model.Dispositivi;

import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Corsia.Verso;

public class Telecamera extends Dispositivi {

    public Telecamera(Integer ID, Boolean status, Corsia corsia, Casello casello) {
        super(ID,status, corsia, casello);
    }

    public void getTarga(){
        // ???? GESTISCE TELEPASS? COME PRENDO LA TARGA? DOVREBBE ESSERE ATTIVATA DA UN SENSORE O RIMANE ATTIVA SEMPRE?
        // SPRECHEREBBE CPU DELL'ARDUINO, FORSE VA GESTITA CON BROKER COME TUTTI GLI ALTRI DISPOSITIVI
        // I DISPOSITIVI DOVREBBERO ESSERE UN ENTITÀ SEPARATA?
    }

}
