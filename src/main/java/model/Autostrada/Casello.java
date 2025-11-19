package model.Autostrada;

import java.util.ArrayList;

public class Casello {

    private String sigla;
    private ArrayList<Corsia> corsie;
    private Boolean isClosed;



    private Integer limite;

    public Casello(String sigla, ArrayList<Corsia> corsie, Integer limite) {
        this.sigla = sigla;
        this.corsie = corsie;   //costruisco corsie altrove e lo passo al costruttore
        this.isClosed = chiudiCasello();
        this.limite = limite;
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

    public Boolean getClosed() {
        return isClosed;
    }
    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public Integer getLimite() {
        return limite;
    }

    public void setLimite(Integer limite) {
        this.limite = limite;
    }

    public boolean chiudiCasello() { //setCasello
        for (Corsia corsia : corsie) {
            if (!corsia.getClosed()) { //per ogni corsia se è aperta ritorna false
                return false;
            }
        }
        return true;
    }
}
