package model.Autostrada;

import java.util.ArrayList;

public class Casello {

    private String sigla;
    private ArrayList<Corsia> corsie;

    public Casello(String sigla, ArrayList<Corsia> corsie) {
        this.sigla = sigla;
        this.corsie = corsie;   //costruisco corsie altrove e lo passo al costruttore
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public ArrayList<Corsia> getCorsie() {
        return corsie;
    }

    public void setCorsie(ArrayList<Corsia> corsie) {
        this.corsie = corsie;
    }
}
