package model.Dispositivi;

import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Corsia.Verso;

public class Sbarra extends Dispositivi {

    public Sbarra(Integer ID, Boolean status, Integer corsia, Integer casello) {
        super(ID,status, corsia, casello);
    }

    public Sbarra(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello);
    }


    public void apriSbarra(){
        // LEGGERE TELECAMERA
    }

    public void chiudiSbarra(){
        //
    }
}