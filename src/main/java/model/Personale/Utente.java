package model.Personale;
import model.Autostrada.Casello;

import java.util.ArrayList;

public class Utente {

    private String user;
    private String password;
    private Boolean isAdmin;

    @Override
    public String toString() {
        return "Utente{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }

    public Utente(String user, String password, Boolean isAdmin) {
        this.user = user;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    // Metodo di utilità
    public boolean isAdministrator() {
        return isAdmin != null && isAdmin;
    }


    public void addDispositivo(){
        //METODO DI CORSIA
    }

    public void addCorsia(){
        // METODO DI CASELLO
    }

    public void RemoveCasello(){
        // VA MESSO SU AUTOSTRADA -----> UN AUTOSTRADA "HAS A" CASELLO
    }

    public void RemoveDispositivo(){
        //METODO DI CORSIA
    }

    public void RemoveCorsia(){
        // METODO DI CASELLO
    }

    public void RemoveAllCorsie(){
        //METODO DI CASELLO
    }

    public void RemoveAllDispositivi(){
        //METODO DI CORSIA
    }
}
