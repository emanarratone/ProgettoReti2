import DB.daoCasello;
import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Regione;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class testCasello {

    @Order(0)
    @Test
    public void testExc(){
        daoCasello dao = new daoCasello();
        Regione r = new Regione(1, "Piemonte");
        Autostrada a = new Autostrada("Alessandria", r.getId());
        Casello c = new Casello("AL", 1, false, 130);
        assertThrows(SQLException.class, ()->{dao.insertCasello(c.getIdAutostrada(), null, c.getLimite(), 50.0);});
    }
/*
    @Order(1)
    @Test
    public void testInsert(){
        daoCasello dao = new daoCasello();
        Regione r = new Regione(1, "Piemonte");
        Autostrada a = new Autostrada("Alessandria", r.getId());
        Casello c = new Casello("AL", 1, false, 130);
        assertDoesNotThrow(SQLException.class, dao.insertCasello(c.getIdAutostrada(), null, c.getLimite(), 50.0));
    }
    */
}
