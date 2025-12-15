package com.example.dispositivi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SBARRA")
public class Sbarra extends Dispositivo {

    public Sbarra() {
        super();
    }

    public Sbarra(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello);
    }

    public Sbarra(Integer ID, Boolean status, Integer corsia, Integer casello) {
        super(ID, status, corsia, casello);
    }
}
