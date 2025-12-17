package com.uniupo.dispositivi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TOTEM")
public class Totem extends Dispositivo {

    public Totem() {
        super();
        this.tipoDispositivo = "TOTEM";
    }

    public Totem(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello, "TOTEM");
    }
}
