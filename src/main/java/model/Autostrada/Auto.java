package model.Autostrada;

import java.util.ArrayList;
import java.util.Random;

public class Auto {

    private String targa;
    private Veicolo tipoVeicolo;
    private static final Random RANDOM = new Random();
    private ArrayList<Multa> multe; //lista delle multe

    private enum Veicolo { AUTO, MOTO, CAMION }

    public Auto(String targa) {
        this.targa = targa;
        this.tipoVeicolo = randomVeicolo();
        this.multe = new ArrayList<>();
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

    public Veicolo getTipoVeicolo() {
        return tipoVeicolo;
    }

    public void setTipoVeicolo(Veicolo tipoVeicolo) {
        this.tipoVeicolo = tipoVeicolo;
    }

    public ArrayList<Multa> getMulte() {
        return multe;
    }

    public void setMulte(ArrayList<Multa> multe) {
        this.multe = multe;
    }
}
