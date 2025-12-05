import DB.daoAutostrada;
import DB.daoCasello;
import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Regione;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class testAutostrada {

    private daoAutostrada dao;
    private Regione r;
    private Autostrada a;

    @BeforeEach
    void setup() {
        dao = new daoAutostrada();
        r =  new Regione(11, "Piemonte");
        a = new Autostrada(22,"Alessandria", r.getId());
    }

    @Order(0)
    @Test
    public void testExcAu(){
        Autostrada aa = new Autostrada(null, r.getId());
        assertThrows(SQLException.class, () -> {dao.insertAutostrada(aa.getCittà(), aa.getIdRegione());});
    }

    @Order(1)
    @Test
    public void testInsertAu(){
        assertDoesNotThrow(()->{dao.insertAutostrada(a.getCittà(), a.getIdRegione());});
    }

    @Order(2)
    @Test
    public void testUpdateAu(){
        Autostrada a = new Autostrada(22, "Alessandria", r.getId());
        assertDoesNotThrow(()->{dao.updateAutostrada(a.getId(), a.getCittà(), a.getIdRegione());});
    }

    @Order(3)
    @Test
    public void testDeleteAu(){
        assertDoesNotThrow(()->{dao.deleteAutostrada(a.getId());});
    }

}