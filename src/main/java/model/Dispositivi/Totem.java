package model.Dispositivi;

import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Corsia.Verso;

public class Totem extends Dispositivi {

    public Totem(Integer ID, Boolean status, Integer corsia, Integer casello) {
        super(ID,status, corsia, casello);
    }

    public Totem(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello);
    }

    public void PagaBiglietto(){
        //
    }
    // !!!!!!!!!!!!!TELEPASS!!!!!!!!!!!!!
    public void generaBiglietto(){
        //
    }
}
