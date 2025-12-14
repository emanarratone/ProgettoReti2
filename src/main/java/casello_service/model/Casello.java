package casello_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "casello")
public class Casello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_casello")
    private Integer idCasello;   // id_casello

    @Column(name = "sigla", nullable = false)
    private String sigla;        // sigla

    @Column(name = "id_autostrada", nullable = false)
    private Integer idAutostrada;// id_autostrada

    @Column(name = "is_closed", nullable = false)
    private boolean closed;      // is_closed

    @Column(name = "limite", nullable = false)
    private Integer limite;      // limite

    public Casello() {}

    public Casello(Integer idCasello, String sigla,
                   Integer idAutostrada, boolean closed, Integer limite) {
        this.idCasello = idCasello;
        this.sigla = sigla;
        this.idAutostrada = idAutostrada;
        this.closed = closed;
        this.limite = limite;
    }

    public Casello(String sigla,
                   Integer idAutostrada, boolean closed, Integer limite) {
        this.sigla = sigla;
        this.idAutostrada = idAutostrada;
        this.closed = closed;
        this.limite = limite;
    }

    public Integer getIdCasello() { return idCasello; }
    public void setIdCasello(Integer idCasello) { this.idCasello = idCasello; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }

    public Integer getIdAutostrada() { return idAutostrada; }
    public void setAutostrada(Integer idAutostrada) { this.idAutostrada = idAutostrada; }

    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }

    public Integer getLimite() { return limite; }
    public void setLimite(Integer limite) { this.limite = limite; }

}
