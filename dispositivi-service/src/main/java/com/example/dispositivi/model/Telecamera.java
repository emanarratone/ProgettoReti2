package com.example.dispositivi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TELECAMERA")
public class Telecamera extends Dispositivo {

    public Telecamera() {
        super();
    }

    public Telecamera(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello);
    }

    public Telecamera(Integer ID, Boolean status, Integer corsia, Integer casello) {
        super(ID, status, corsia, casello);
    }
}
