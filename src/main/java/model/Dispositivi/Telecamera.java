package model.Dispositivi;

public class Telecamera extends Dispositivi {

    public Telecamera(String ID, Boolean status, Tipo tipo){
        super(ID,status, tipo);
    }

    public void getTarga(){
        // ???? GESTISCE TELEPASS? COME PRENDO LA TARGA? DOVREBBE ESSERE ATTIVATA DA UN SENSORE O RIMANE ATTIVA SEMPRE?
        // SPRECHEREBBE CPU DELL'ARDUINO, FORSE VA GESTITA CON BROKER COME TUTTI GLI ALTRI DISPOSITIVI
        // I DISPOSITIVI DOVREBBERO ESSERE UN ENTITÀ SEPARATA?
    }

}
