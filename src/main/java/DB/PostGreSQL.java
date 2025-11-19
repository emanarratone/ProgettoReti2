package DB;

import java.sql.*;

public class PostGreSQL {
    public static void main(String[] args){

        // Register the PostgreSQL driver
        try{
            String jdbcUrl = "jdbc:postgresql://localhost:5432/database_name";
            String username = "username";
            String password = "password";
            Connection con;
            Statement st;
            Class.forName("org.postgresql.Driver");
            // Connect to the database
            con = DriverManager.getConnection(jdbcUrl, username, password);
            // Perform desired database operations
            st= con.createStatement();
            // Close the connection
            con.close();
        }
        catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }

    }
}
