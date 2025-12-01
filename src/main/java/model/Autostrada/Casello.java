package model.Autostrada;

import java.util.ArrayList;

public class Casello {

    private final Integer id;
    private String sigla;
    private Integer autostrada;
    private Boolean isClosed;
    private Integer limite;

    public Casello(Integer id, String sigla, Integer autostrada, Integer limite) {
        this.id = id;
        this.sigla = sigla;
        this.autostrada = autostrada;
        this.isClosed = false;  //chiudi casello da sistemare e aggiungere chiudi corsia
        //this.isClosed = chiudiCasello();
        this.limite = limite;
    }

    public Integer getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
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

    public Integer getAutostrada() {
        return autostrada;
    }

    public void setAutostrada(Integer autostrada) {
        this.autostrada = autostrada;
    }
/*
    public boolean chiudiCasello() { //setCasello
        for (Corsia corsia : corsie) {
            if (!corsia.getClosed()) { //per ogni corsia se è aperta ritorna false
                return false;
            }
        }
        return true;
    }
*/
    @Override
    public String toString() {
        return "Casello " + this.sigla + ":\n" +
                "Autostrada: " + this.autostrada + "\n" +
                "Limite: " + this.limite + "\n" +
                "Stato: " + (this.isClosed ? "chiuso" : "aperto");
    }

}
