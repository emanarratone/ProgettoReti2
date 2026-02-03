import DB.daoMulte;
//import auto_service.model.Auto;
import model.Autostrada.*;
import model.Dispositivi.Dispositivi;
import model.Dispositivi.Totem;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class testMulte {
/*
    private daoMulte dao;
    private Regione r;
    private Autostrada a;
    private Casello c;
    private Corsia co;
    private Dispositivi d;
    private Auto aa;
    private Biglietto b;
    private Multa m;

    @BeforeEach
    void setup(){
        dao = new daoMulte();
        r =  new Regione(11, "Piemonte");
        a = new Autostrada(23,"Alessandria", r.getId());
        c = new Casello(4,"AL", 23, false, 130);
        co = new Corsia(c.getIdCasello(), 2, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        d = new Totem(2, true, co.getNumCorsia(), co.getCasello());
        aa = new Auto("AB123CD");
        b = new Biglietto(4, d.getID(), aa.getTarga(), LocalDateTime.now(), d.getCasello());
        m = new Multa(b.getID_biglietto(), 25.0, aa.getTarga());
    }

    @Order(1)
    @Test
    void testExc(){
        Multa m1 = new Multa(b.getID_biglietto(), 25.0, "PippoPlutoPaperino");
        assertThrows(SQLException.class, () -> {dao.insertMulta(m1);});
    }

    @Order(2)
    @Test
    void testInsert(){
        assertDoesNotThrow(() -> {dao.insertMulta(m);});
    }

    @Order(3)
    @Test
    void testUpdate(){
        Multa m1 = new Multa(1, b.getID_biglietto(), 25.0, aa.getTarga());
        assertDoesNotThrow(() -> {dao.updateMulta(m1, m1);});
    }

    @Order(4)
    @Test
    void testDelete(){
        Multa m1 = new Multa(1, b.getID_biglietto(), 25.0, aa.getTarga());
        assertDoesNotThrow(() -> {dao.deleteMulta(m1);});
    }

 */
}
