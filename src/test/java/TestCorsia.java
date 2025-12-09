import DB.daoCorsia;
import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Regione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
public class TestCorsia {

    private daoCorsia dao;
    private Regione r;
    private Autostrada a;
    private Casello c;
    private Corsia co;

    @BeforeEach
    void setup() {
        dao = new daoCorsia();
        r = new Regione(3, "Piemonte");
        a = new Autostrada(4, "Alessandria", r.getId());
        c = new Casello(4, "AL", a.getId(), false, 130);
        co = new Corsia(c.getIdCasello(),1, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
    }

    @Order(0)
    @Test
    public void testExc(){
        //assertThrows(SQLException.class, () -> {dao.insertCorsia(co.getCasello(), null);});
    }

    @Order(1)
    @Test
    public void testInsert(){
        //assertDoesNotThrow(() -> {dao.insertCorsia(co.getCasello(), co.getVerso().toString());});
    }

    @Order(2)
    @Test
    public void testUpdate(){
        //assertDoesNotThrow(() -> {dao.insertCorsia(co.getCasello(),co.getVerso().toString());});
    }

    @Order(3)
    @Test
    public void testDelete(){
        assertDoesNotThrow(()->{dao.deleteCorsia(co.getNumCorsia(), co.getCasello());});
    }

}
