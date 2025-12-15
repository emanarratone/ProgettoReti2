package corsia_service.model;


import jakarta.persistence.*;

@Entity
@Table(name = "corsia")
public class Corsia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_casello")
    private  Integer casello;

    @Column(name = "num_corsia", nullable = false)
    private  Integer numCorsia;

    @Column(name = "verso", nullable = false)
    private model.Autostrada.Corsia.Verso verso;

    @Column(name = "tipo", nullable = false)
    private model.Autostrada.Corsia.Tipo tipo;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed;

    public Corsia() {}

    public enum Tipo {MANUALE, TELEPASS}
    public enum Verso { ENTRATA, USCITA }

    public Corsia(Integer casello, Integer numCorsia, model.Autostrada.Corsia.Verso verso, model.Autostrada.Corsia.Tipo tipo) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = false;
    }

    public Corsia(Integer casello, Integer numCorsia, model.Autostrada.Corsia.Verso verso, model.Autostrada.Corsia.Tipo tipo, boolean isClosed) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = isClosed;
    }

    public model.Autostrada.Corsia.Tipo getTipo() {
        return tipo;
    }

    public void setTipo(model.Autostrada.Corsia.Tipo tipo) {
        this.tipo = tipo;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
        isClosed = closed;
    }

    public Integer getCasello() {
        return casello;
    }

    public model.Autostrada.Corsia.Verso getVerso() {
        return verso;
    }

    public void setVerso(model.Autostrada.Corsia.Verso verso) {
        this.verso = verso;
    }

    public Integer getNumCorsia() {
        return numCorsia;
    }

    @Override
    public String toString() {
        return "Casello "+ this.casello + "\n" +
                "ID "+ this.numCorsia + "\n" +
                "Stato: " + (this.isClosed ? "aperto" : "chiuso");
    }

}
