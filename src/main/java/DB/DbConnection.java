package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static String URL = "jdbc:postgresql://localhost:5432/autostrada"; //url DB
    private static String USER = "postgres"; //user
    private static String PASS = "admin"; //Password

    static {
        try {
            Class.forName("org.postgresql.Driver"); // registra il driver una volta sola
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL non trovato", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}