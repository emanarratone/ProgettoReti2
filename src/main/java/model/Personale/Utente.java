package model.Personale;

import model.Autostrada.Casello;

import java.util.ArrayList;

public class Utente {

    private String user;
    private String password;
    private Boolean isAdmin;

    public Utente(String user, String password,  Boolean isAdmin) {
        this.user = user;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public void login(String username, String password) {
        //
    }

    public Casello visualizzaCasello(Casello casello) {
        return casello; //lololol
        //visualizza corsie()
        //visualizza dispositivi()
    }


    public void visualizzaMulte(){
        //
    }

    public void addCasello(){
        //
    }


    public void addDispositivo(){
        //
    }

    public void addCorsia(){
        //
    }


    public void RemoveCasello(){
        //
    }


    public void RemoveDispositivo(){
        //
    }

    public void RemoveCorsia(){
        //
    }

    public void RemoveAllCorsie(){
        //se elimino un casello
    }

    public void RemoveAllDispositivi(){
        //se elimino un casello
    }
}
