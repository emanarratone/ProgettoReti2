import DB.daoUtente;
import model.Personale.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class testUtente {

    private daoUtente dao;
    private Utente u;

    @BeforeEach
    void setup() {
        dao = new daoUtente();
        u = new Utente ("admin","admin",true);
    }

    @Order(1)
    @Test
    public void testRegistrazione(){
        assertDoesNotThrow(() -> {
            daoUtente.registrazione(u.getUser(), BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()),u.getIsAdmin());});
    }

    @Order(2)
    @Test
    public void testLogin() throws Exception {
        daoUtente.registrazione(u.getUser(), BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()),u.getIsAdmin());
        String s = daoUtente.getHashedPassword(u.getUser());
        assertTrue(BCrypt.checkpw(u.getPassword(), s));
    }

}
