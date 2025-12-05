import DB.daoCorsia;
import model.Autostrada.Autostrada;
import model.Autostrada.Casello;
import model.Autostrada.Corsia;
import model.Autostrada.Regione;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestCorsia {

    private daoCorsia dao;
    private Regione r;
    private Autostrada a;
    private Casello c;
    private Corsia co;

    @BeforeEach
    void setup() {
        dao = new daoCorsia();
        r = new Regione(1, "Piemonte");
        a = new Autostrada("Alessandria", r.getId());
        c = new Casello("AL", 23, false, 130);
    }

    @Order(0)
    @Test
    public void testExcAu(){
        co = new Corsia(c,null, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        assertThrows(NullPointerException.class, () -> {dao.insertCorsia(co.getCasello().getIdCasello(),co.getVerso().toString());});
    }

    @Order(1)
    @Test
    public void testInsertCo(){
        co = new Corsia(c,1, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        assertThrows(NullPointerException.class, () -> {dao.insertCorsia(co.getCasello().getIdCasello(),co.getVerso().toString());});
    }

    @Order(2)
    @Test
    public void testUpdateAu(){
        co = new Corsia(c,1, Corsia.Verso.USCITA, Corsia.Tipo.TELEPASS);
        assertThrows(NullPointerException.class, () -> {dao.insertCorsia(co.getCasello().getIdCasello(),co.getVerso().toString());});
    }

    @Order(3)
    @Test
    public void testDeleteAu(){
        co = new Corsia(c,1, Corsia.Verso.ENTRATA, Corsia.Tipo.MANUALE);
        assertDoesNotThrow(()->{dao.deleteCorsia(co.getNumCorsia());});
    }


}
