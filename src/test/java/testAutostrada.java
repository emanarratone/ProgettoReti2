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
        Regione r =  new Regione(1, "Piemonte");
        Autostrada a = new Autostrada("Alessandria", r.getId());
        assertDoesNotThrow(()->{dao.insertAutostrada(a.getCittà(), a.getIdRegione());});
    }

    @Order(2)
    @Test
    public void testUpdateAu(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(1, "Piemonte");
        Autostrada a = new Autostrada("Alessandria", r.getId());
        Autostrada a1 = new Autostrada("Alessandria", r.getId());
        assertDoesNotThrow(()->{dao.aggiornaAutostrada(a, a1);});
    }

    @Order(3)
    @Test
    public void testDeleteAu(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(1, "Piemonte");
        Autostrada a = new Autostrada(1, "Alessandria", r.getID());
        assertDoesNotThrow(()->{dao.eliminaAutostrada(a);});
    }

    @Order(4)
    @Test
    public void testExcRe(){
        daoAutostrada dao = new daoAutostrada();
        Regione r = new Regione(1, null);
        assertThrows(SQLException.class, () -> {dao.insertRegione(r);});
    }


    @Order(5)
    @Test
    public void testinsertRe(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(1, "Piemonte");
        assertDoesNotThrow(()->{dao.insertRegione(r);});
    }
/*
    @Order(6)
    @Test
    public void testupdateRe(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(1, "Piemonte");
        assertDoesNotThrow(()->{dao.updateRegione(1, r);});
    }

    @Order(7)
    @Test
    public void testdeleteRe(){
        daoAutostrada dao = new daoAutostrada();
        Regione r =  new Regione(1, "Piemonte");
        assertDoesNotThrow(()->{dao.deleteRegione(1);});
    }
*/
}