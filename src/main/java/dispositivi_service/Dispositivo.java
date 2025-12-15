package dispositivi_service;

import jakarta.persistence.*;

@Entity
@Table(name = "dispositivo")
public abstract class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_casello")
    protected Integer ID;

    @Column(name = "num_corsia", nullable = false)
    private Integer corsia;

    @Column(name = "id_casello", nullable = false)
    private Integer casello;

    @Column(name = "stato", nullable = false)
    protected Boolean status; //guasto = 0 o funzionante = 1

    public Dispositivo(Integer ID, Boolean status, Integer corsia,  Integer casello) {
        this.ID = ID;
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
    }

    public Dispositivo(Boolean status, Integer corsia,  Integer casello) {
        this.status = status;
        this.corsia = corsia;
        this.casello = casello;
    }

    public Dispositivo(){}

    public Integer getCorsia() {
        return corsia;
    }

    public Integer getID() { return ID; }

    public Integer getCasello() { return casello; }

    public String getStatus() {
        return (status)? "ATTIVO" : "INATTIVO";
    }

    public void setStatus(Boolean status) {this.status = status;}

}

