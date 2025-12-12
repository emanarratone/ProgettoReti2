package regione_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "REGIONE")
public class Regione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REGIONE")
    private Integer id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    public Regione() {}

    public Regione(String nome) {
        this.nome = nome;
    }

    public Regione(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
