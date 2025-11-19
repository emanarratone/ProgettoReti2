import DB.DbConnection;
import DB.daoBiglietto;   // quando esiste
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println(daoBiglietto.countAutoOggi());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
