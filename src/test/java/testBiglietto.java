import DB.daoBiglietto;
import DB.daoDispositivi;
import model.Autostrada.*;
import model.Dispositivi.Dispositivi;
import model.Dispositivi.Sbarra;
import model.Dispositivi.Totem;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class testBiglietto {

    private daoBiglietto dao;
    private Regione r;
    private Autostrada a;
    private Casello c;
    private Corsia co;
    private Dispositivi d;
    private Auto aa;
    private Biglietto b;


    @BeforeEach
    void setUp(){
        dao = new daoBiglietto();
        r =  new Regione(11, "Piemonte");
        a = new Autostrada(23,"Alessandria", r.getId());
        c = new Casello(4,"AL", 23, false, 130);
        co = new Corsia(c.getIdCasello(), 2, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        d = new Totem(2, true, co.getNumCorsia(), co.getCasello());
        aa = new Auto("AB123CD");
        b = new Biglietto(d.getID(), aa.getTarga(), LocalDateTime.now(), d.getCasello());//IL CASELLO VA PRESO DAL TOTEM, NON ALTROVE O POTREMMO AVERE PROBLEMI
    }

    @Order(1)
    @Test
    void testExc(){
        assertThrows(SQLException.class, ()-> dao.insertBiglietto(b));
    }

    @Order(2)
    @Test
    void testInsert(){
        assertDoesNotThrow(()->{dao.insertBiglietto(b);});
    }

    @Order(3)
    @Test
    void testUpdate(){
        Biglietto bb = new Biglietto(1, d.getID(), aa.getTarga(), LocalDateTime.now(), d.getCasello());    //l'importante è che venga fatto, non cosa viene fatto
        assertDoesNotThrow(()->{dao.aggiornaBiglietto(bb, bb);});
    }

    @Order(4)
    @Test
    void testDelete(){
        Biglietto bbb =  new Biglietto(3, d.getID(), aa.getTarga(), LocalDateTime.now(), d.getCasello());
        assertDoesNotThrow(()->{dao.eliminaBiglietto(bbb);});
    }

}
