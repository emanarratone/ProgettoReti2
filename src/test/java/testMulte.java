import DB.daoMulte;
import model.Autostrada.*;
import model.Dispositivi.Dispositivi;
import model.Dispositivi.Totem;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class testMulte {

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
        r =  new Regione(11, "Piemonte");
        a = new Autostrada(23,"Alessandria", r.getId());
        c = new Casello(4,"AL", 23, false, 130);
        co = new Corsia(c.getIdCasello(), 2, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        d = new Totem(2, true, co.getNumCorsia(), co.getCasello());
        aa = new Auto("AB123CD");
        b = new Biglietto(4, d.getID(), aa.getTarga(), LocalDateTime.now(), d.getCasello());
        m = new Multa(b.getID_biglietto(), 25.0, LocalDateTime.now(), aa.getTarga());
    }

    @Order(1)
    @Test
    void testExc(){
        assertThrows(SQLException.class, () -> {dao.insertMulta(null);});
    }
}
