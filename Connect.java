//Author DA
//connect class to connect to the database

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect 
{
   public static Connection getConnection() throws SQLException 
   {
   	Connection conn = null;
   	String url = "jdbc:mysql://localhost:3306/db_atm";
   	String user = "student";
   	String password = "student";
   	conn =  DriverManager.getConnection(url, user, password);
	   return conn;
    }
   
   
}
