import DB.daoCasello;
import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Regione;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class testCasello {

    private daoCasello dao;
    private Regione r;
    private Autostrada a;
    private Casello c;

    @BeforeEach
    void setup() {
        dao = new daoCasello();
        r = new Regione(1, "Piemonte");
        a = new Autostrada("Alessandria", r.getId());
        c = new Casello("AL", 23, false, 130);
    }

    @Order(0)
    @Test
    public void testExc(){
        assertThrows(SQLException.class, ()->{dao.insertCasello(c.getIdAutostrada(), null, c.getLimite(), 50.0);});
    }

    @Order(1)
    @Test
    public void testInsert(){
        assertDoesNotThrow(()-> dao.insertCasello(c.getIdAutostrada(), c.getSigla(), c.getLimite(), 50.0));
    }

    @Order(2)
    @Test
    public void testUpdate(){
        daoCasello dao = new daoCasello();

    }

}
