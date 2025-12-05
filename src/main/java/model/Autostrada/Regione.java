package model.Autostrada;


public class Regione {

    private Integer id;        // id_regione
    private String nomeRegione;

    public Regione(Integer id, String nomeRegione) {
        this.id = id;
        this.nomeRegione = nomeRegione;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeRegione() { return nomeRegione; }
    public void setNomeRegione(String nomeRegione) { this.nomeRegione = nomeRegione;
    }
}
