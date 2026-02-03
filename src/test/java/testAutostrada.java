import DB.daoAutostrada;
import model.Autostrada.Autostrada;
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
        r =  new Regione(3, "Piemonte");
        a = new Autostrada(22,"Alessandria", r.getId());
    }

    @Order(0)
    @Test
    public void testExcAu(){
        Autostrada aa = new Autostrada(null, r.getId());
        assertThrows(SQLException.class, () -> {dao.insertAutostrada(aa.getSigla(), aa.getIdRegione());});
    }

    @Order(1)
    @Test
    public void testInsertAu(){
        assertDoesNotThrow(()->{dao.insertAutostrada(a.getSigla(), a.getIdRegione());});
    }

    @Order(2)
    @Test
    public void testUpdateAu(){
        Autostrada a = new Autostrada(22, "Alessandria", r.getId());
        assertDoesNotThrow(()->{dao.updateAutostrada(a.getId(), a.getSigla(), a.getIdRegione());});
    }

    @Order(3)
    @Test
    public void testDeleteAu(){
        assertDoesNotThrow(()->{dao.deleteAutostrada(a.getId());});
    }

}