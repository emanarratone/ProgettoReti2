import DB.daoRegione;
import model.Autostrada.Regione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class testRegione {

    private daoRegione dao;
    private Regione r;

    @BeforeEach
    void setup() {
        dao = new daoRegione();
        r = new Regione(1, "Piemonte");
    }

    @Order(1)
    @Test
    public void testExcRe(){
        Regione r = new Regione(1, null);
        assertThrows(SQLException.class, () -> {dao.insertRegione(r);});
    }

    @Order(2)
    @Test
    public void testinsertRe(){
        assertDoesNotThrow(()->{dao.insertRegione(r);});
    }

    @Order(3)
    @Test
    public void testupdateRe(){
        assertDoesNotThrow(()->{dao.updateRegione(1, r.getNomeRegione());});
    }

    @Order(4)
    @Test
    public void testdeleteRe(){
        assertDoesNotThrow(()->{dao.deleteRegione(1);});
    }
}
