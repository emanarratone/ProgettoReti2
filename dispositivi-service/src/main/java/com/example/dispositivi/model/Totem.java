package com.example.dispositivi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TOTEM")
public class Totem extends Dispositivo {

    public Totem() {
        super();
    }

    public Totem(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello);
    }

    public Totem(Integer ID, Boolean status, Integer corsia, Integer casello) {
        super(ID, status, corsia, casello);
    }
}
