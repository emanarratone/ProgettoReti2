package model.Dispositivi;

import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Corsia.Verso;

public class Totem extends Dispositivi {

    public Totem(Integer ID, Boolean status, Corsia corsia, Casello casello) {
        super(ID,status, corsia, casello);
    }

    public void PagaBiglietto(){
        //
    }
    // !!!!!!!!!!!!!TELEPASS!!!!!!!!!!!!!
    public void generaBiglietto(){
        //
    }
}
