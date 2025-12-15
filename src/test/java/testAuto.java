import DB.daoVeicoli;
import auto_service.model.Auto;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;



public class testAuto {

    private daoVeicoli dao;
    private Auto a;

    @BeforeEach
    void setUp() {
        dao = new daoVeicoli();
        a = new Auto("AB123CD");
    }

    @Order(1)
    @Test
    void testExc(){
        Auto auto = new Auto(null);
        assertThrows(SQLException.class, ()->dao.insertVeicoli(auto));
    }

    @Order(2)
    @Test
    void testInsert(){
        assertDoesNotThrow(()->{dao.insertVeicoli(a);});    // se da errore cambiare la targa a riga 18...è chiave!
    }

    @Order(3)
    @Test
    void testDelete(){
        assertDoesNotThrow(()->{dao.deleteVeicolo(a.getTarga());});
    }
}
