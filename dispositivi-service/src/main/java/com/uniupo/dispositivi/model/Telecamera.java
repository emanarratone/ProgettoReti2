package com.uniupo.dispositivi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TELECAMERA")
public class Telecamera extends Dispositivo {
    public Telecamera() {
        super();
        this.tipoDispositivo = "TELECAMERA";
    }
    public Telecamera(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello, "TELECAMERA");
    }
}
