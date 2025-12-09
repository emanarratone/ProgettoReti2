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
        r = new Regione(3, "Piemonte");
        a = new Autostrada(4,"Alessandria", r.getId());
        c = new Casello("AL", a.getId(), false, 130);
    }

    @Order(0)
    @Test
    public void testExc(){
        assertThrows(SQLException.class, ()->{dao.insertCasello(c.getIdAutostrada(), null, c.getLimite());});
    }

    @Order(1)
    @Test
    public void testInsert(){
        assertDoesNotThrow(()-> dao.insertCasello(c.getIdAutostrada(), c.getSigla(), c.getLimite()));
    }

    @Order(2)
    @Test
    public void testUpdate(){
        assertDoesNotThrow(()-> dao.updateCasello(3, c.getSigla(), 22, false)); //servono gli id corretti o non va
    }

    @Order(3)
    @Test
    public void testDelete(){
        assertDoesNotThrow(()-> dao.deleteCasello(3));
    }

}
