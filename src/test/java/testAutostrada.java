import DB.daoAutostrada;
import model.Autostrada.Autostrada;
import model.Autostrada.Regione;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class testAutostrada {

    @Order(0)
    @Test
    public void testExcAu(){
        daoAutostrada dao = new daoAutostrada();
        Regione r = new Regione(1, "Piemonte");
        Autostrada a = new Autostrada(null, r.getId());
        assertThrows(SQLException.class, () -> {dao.insertAutostrada(a.getCittà(), a.getIdRegione());});
    }

    @Order(1)
    @Test
    public void testInsertAu(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(11, "Piemonte");
        Autostrada a = new Autostrada("Alessandria", r.getId());
        assertDoesNotThrow(()->{dao.insertAutostrada(a.getCittà(), a.getIdRegione());});
    }

    @Order(2)
    @Test
    public void testUpdateAu(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(11, "Piemonte");
        Autostrada a = new Autostrada(22, "Alessandria", r.getId());
        assertDoesNotThrow(()->{dao.updateAutostrada(a.getId(), a.getCittà(), a.getIdRegione());});
    }

    @Order(3)
    @Test
    public void testDeleteAu(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(11, "Piemonte");
        Autostrada a = new Autostrada(22,"Alessandria", r.getId());
        assertDoesNotThrow(()->{dao.deleteAutostrada(a.getId());});
    }

}