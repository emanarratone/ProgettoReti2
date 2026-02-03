import DB.daoPagamenti;
//import auto_service.model.Auto;
import model.Autostrada.*;
import model.Dispositivi.Dispositivi;
import model.Dispositivi.Totem;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class testPagamenti {
/*
    private daoPagamenti dao;
    private Regione r;
    private Autostrada a;
    private Casello c;
    private Corsia co;
    private Dispositivi d;
    private Auto aa;
    private Biglietto b;
    private Pagamento p;


    @BeforeEach
    void setUp(){
        dao = new daoPagamenti();
        r =  new Regione(11, "Piemonte");
        a = new Autostrada(23,"Alessandria", r.getId());
        c = new Casello(4,"AL", 23, false, 130);
        co = new Corsia(c.getIdCasello(), 2, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        d = new Totem(2, true, co.getNumCorsia(), co.getCasello());
        aa = new Auto("AB123CD");
        b = new Biglietto(4, d.getID(), aa.getTarga(), LocalDateTime.now(), d.getCasello());
        p= new Pagamento(b.getID_biglietto(), 30.0, true, LocalDateTime.now(), c.getIdCasello());
    }

    @Order(1)
    @Test
    public void testExc(){
        Pagamento p1 = new Pagamento(b.getID_biglietto(), 330.0, true, LocalDateTime.now(), 123);
        assertThrows(SQLException.class, ()->dao.insertPagamenti(p1));
    }

    @Order(2)
    @Test
    public void testInsert(){
        assertDoesNotThrow(()->dao.insertPagamenti(p));
    }

    @Order(3)
    @Test
    public void testUpdate(){
        assertDoesNotThrow(()->dao.updatePagamento(1, p));
    }

    @Order(4)
    @Test
    public void testDelete(){
        assertDoesNotThrow(()->dao.deletePagamento(1));
    }

 */

}
