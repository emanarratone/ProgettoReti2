import DB.daoRegione;
import model.Autostrada.Regione;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class testRegione {

    @Order(4)
    @Test
    public void testExcRe(){
        daoRegione dao = new daoRegione();
        Regione r = new Regione(1, null);
        assertThrows(SQLException.class, () -> {dao.insertRegione(r.getNomeRegione());});
    }

    @Order(5)
    @Test
    public void testinsertRe(){
        daoRegione dao = new daoRegione();
        Regione r =  new Regione(1, "Piemonte");
        assertDoesNotThrow(()->{dao.insertRegione(r.getNomeRegione());});
    }

    @Order(6)
    @Test
    public void testupdateRe(){
        daoRegione dao = new daoRegione();
        Regione r =  new Regione(1, "Piemonte");
        assertDoesNotThrow(()->{dao.updateRegione(1, r.getNomeRegione());});
    }

    @Order(7)
    @Test
    public void testdeleteRe(){
        daoRegione dao = new daoRegione();
        Regione r =  new Regione(1, "Piemonte");
        assertDoesNotThrow(()->{dao.deleteRegione(1);});
    }
}
