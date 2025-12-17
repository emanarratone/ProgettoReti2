package com.uniupo.dispositivi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SBARRA")
public class Sbarra extends Dispositivo {
    public Sbarra() {
        super();
        this.tipoDispositivo = "SBARRA";
    }
    public Sbarra(Boolean status, Integer corsia, Integer casello) {
        super(status, corsia, casello, "SBARRA");
    }
}

