package DB;

import java.sql.*;

public class PostGreSQL {
    public static void main(String[] args){

        try{
            // Register the PostgreSQL driver
            String jdbcUrl = "jdbc:postgresql://localhost:5432/autostrada";
            String username = "postgres";
            String password = "admin";
            Connection con;
            Statement st;
            Class.forName("org.postgresql.Driver");

            // Connect to the database
            con = DriverManager.getConnection(jdbcUrl, username, password);

            // Perform desired database operations
            st= con.createStatement();
            String query = "select * from Elenco";
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                System.out.println(rs.getString(2)); //tutta la colonna 2 (secondo campo)
            }
            // Close the connection
            con.close();
        }
        catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }

    }
}
