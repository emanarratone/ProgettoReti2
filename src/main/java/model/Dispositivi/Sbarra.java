package model.Dispositivi;

import model.Autostrada.Corsia.Tipo;

public class Sbarra extends Dispositivi {

    public Sbarra(Integer ID, Boolean status, Tipo tipo, Integer corsia) {
        super(ID,status,tipo, corsia);
    }

    public void apriSbarra(){
        // LEGGERE TELECAMERA
    }

    public void chiudiSbarra(){
        //
    }
}