package model.Autostrada;

public class Autostrada {

    private Integer id;       // id_autostrada
    private final String sigla;     // nome autostrada/città
    private final Integer idRegione;

    public Autostrada(String sigla, Integer idRegione) {
        this.sigla = sigla;
        this.idRegione = idRegione;
    }

    public Autostrada(Integer id, String sigla, Integer idRegione) {
        this.id = id;
        this.sigla = sigla;
        this.idRegione = idRegione;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSigla() { return sigla; }

    public Integer getIdRegione() { return idRegione; }
}
