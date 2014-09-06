package mmqa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlConnection {

	public static void main(String[] args) {
		System.out.println(Util.getConfigProperties(MySqlConnection.class.getClass().getClassLoader().getResource("/").getPath()));
		
		System.out.println("-------- MySQL JDBC Connection Testing ------------");
		 
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
	 
		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;
	 
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root", "root");
			if (connection != null) {
				System.out.println("You made it, take control your database now!");
			} else {
				System.out.println("Failed to make connection!");
			}
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		} finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	 
		
	}
	
	public static Connection getConnection(){
		Connection connection = null;
		
		//System.out.println(Util.getConfigProperties(MySqlConnection.class.getClass().getClassLoader().getResource("/").getPath()));
		try {
			Properties prop = Util.getProperties(Util.class.getClassLoader().getResource("/").getPath(), "db.properties");
			Class.forName("com.mysql.jdbc.Driver");
			//connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root", "root");
			connection = DriverManager.getConnection(prop.getProperty("dburl"),prop.getProperty("dbuser"), prop.getProperty("dbpwd"));
			
			if (connection != null) {
				connection.setAutoCommit(false);
				System.out.println("You made it, take control your database now!");
			} else {
				System.out.println("Failed to make connection!");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();		
		} finally {
			
		}	
		return connection;
	}

}
