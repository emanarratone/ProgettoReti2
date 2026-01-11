package com.uniupo.corsia.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "corsia")
@IdClass(Corsia.CorsiaId.class)
public class Corsia {

    @Id
    @Column(name = "num_corsia", nullable = false)
    private Integer numCorsia;

    @Id
    @Column(name = "id_casello", nullable = false)
    private Integer casello;

    @Enumerated(EnumType.STRING)
    @Column(name = "verso", nullable = false)
    private Verso verso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_corsia", nullable = false)
    private Tipo tipo;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed;

    // ENUM INTERNI
    public enum Tipo {
        NORMALE, TELEPASS, EMERGENZA
    }

    public enum Verso {
        ENTRATA, USCITA
    }

    // IDCLASS INTERNA - tutto nello stesso file
    @Embeddable
    public static class CorsiaId implements Serializable {
        private Integer numCorsia;
        private Integer casello;

        public CorsiaId() {}

        public CorsiaId(Integer numCorsia, Integer casello) {
            this.numCorsia = numCorsia;
            this.casello = casello;
        }

        // GETTERS IDCLASS
        public Integer getNumCorsia() { return numCorsia; }
        public void setNumCorsia(Integer numCorsia) { this.numCorsia = numCorsia; }
        public Integer getCasello() { return casello; }
        public void setCasello(Integer casello) { this.casello = casello; }

        // EQUALS + HASHCODE OBBLIGATORI PER JPA
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CorsiaId corsiaId = (CorsiaId) o;
            return Objects.equals(numCorsia, corsiaId.numCorsia) &&
                    Objects.equals(casello, corsiaId.casello);
        }

        @Override
        public int hashCode() {
            return Objects.hash(numCorsia, casello);
        }
    }

    // COSTRUTTORI COMPLETI
    public Corsia() {}

    public Corsia(Integer casello, Integer numCorsia, Verso verso, Tipo tipo) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = false;
    }

    public Corsia(Integer casello, Integer numCorsia, Verso verso, Tipo tipo, boolean isClosed) {
        this.casello = casello;
        this.numCorsia = numCorsia;
        this.verso = verso;
        this.tipo = tipo;
        this.isClosed = isClosed;
    }

    // GETTERS E SETTERS COMPLETI
    public Integer getNumCorsia() { return numCorsia; }
    public void setNumCorsia(Integer numCorsia) { this.numCorsia = numCorsia; }

    public Integer getCasello() { return casello; }
    public void setCasello(Integer casello) { this.casello = casello; }

    public Verso getVerso() { return verso; }
    public void setVerso(Verso verso) { this.verso = verso; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public Boolean getIsClosed() { return isClosed; }
    public void setIsClosed(Boolean isClosed) { this.isClosed = isClosed; }
    public void setClosed(boolean closed) { this.isClosed = closed; }
    public Boolean getClosed() { return isClosed; }

    @Override
    public String toString() {
        return "Corsia{casello=" + casello +
                ", numCorsia=" + numCorsia +
                ", verso=" + verso +
                ", tipo=" + tipo +
                ", isClosed=" + isClosed + "}";
    }
}
