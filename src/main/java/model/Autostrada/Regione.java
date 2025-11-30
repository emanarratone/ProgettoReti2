package model.Autostrada;

public class Regione {

    private String ID;
    private String nome;

    public Regione(String ID, String nome) {
        this.ID = ID;
        this.nome = nome;
    }

    public String getID() {
        return ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
