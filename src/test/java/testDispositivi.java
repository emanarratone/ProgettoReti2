import DB.daoDispositivi;
import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Regione;
import model.Dispositivi.Dispositivi;
import model.Dispositivi.Totem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class testDispositivi {

    private daoDispositivi dao;
    private Regione r;
    private Autostrada a;
    private Casello c;
    private Corsia co;
    private Dispositivi d;

    @BeforeEach
    void setup() {
        dao = new daoDispositivi();
        r =  new Regione(3, "Piemonte");
        a = new Autostrada(4,"Alessandria", r.getId());
        c = new Casello(4,"AL", 23, false, 130);
        co = new Corsia(c.getIdCasello(), 2, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        d = new Totem(true, co.getNumCorsia(), co.getCasello());   //il casello va SEMPRE OBBLIGATORIAMENTE preso dalla corsia e non direttamente dal casello o potremmo avere problemi
    }

    @Order(0)
    @Test
    public void testExc(){
        assertThrows(SQLException.class, () -> {dao.insertDispositivo(co.getNumCorsia(), null, co.getCasello());});
    }

    @Order(1)
    @Test
    public void testInsert(){
        assertDoesNotThrow(()->{dao.insertDispositivo(co.getNumCorsia(), dao.getTipoDispositivo(d), co.getCasello());});
    }

    @Order(2)
    @Test
    public void testUpdate(){
       assertDoesNotThrow(()->{dao.updateDispositivo(2, d.getStatus());});
    }

    @Order(3)
    @Test
    public void testDelete(){
        assertDoesNotThrow(()->{dao.deleteDispositivo(4);});
    }
    //nella realtà prendo l'id da un form in chiaro, qui non posso astrarre l'id quindi devo obbligatoriamente dichiararlo esplicitamente riferendomi al db.
    //funziona solo se in db ho un dispositivo con id=4, se nel vostro db non lo avete, cambiate idDispositivo con quello presente nel db
}