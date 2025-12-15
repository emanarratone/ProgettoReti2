package auto_service.model;

import jakarta.persistence.*;
import java.util.Random;

@Entity
@Table(name = "auto")
public class Auto {

    @Id
    @Column(name = "targa")
    private String targa;

    @Column(name = "classe_veicolo")
    private Veicolo classe_veicolo;
    private static final Random RANDOM = new Random();

    public enum Veicolo { A, B, C, D, E }

    public Auto(String targa) {
        this.targa = targa;
        this.classe_veicolo = randomVeicolo();
    }

    private Veicolo randomVeicolo() {
        Veicolo[] values = Veicolo.values();
        return values[RANDOM.nextInt(values.length)];
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public Veicolo getclasse_veicolo() {
        return classe_veicolo;
    }

}
