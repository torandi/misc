package model;


import java.sql.*;

import javax.naming.*;
import javax.sql.*;

public class DatabaseConnection {
	private Connection connection;
	private static DatabaseConnection singleton = null;
	
	public DatabaseConnection() throws NamingException, SQLException {
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource) envCtx.lookup("jdbc/db");
		connection = ds.getConnection();
	}
	
	public static DatabaseConnection get() {
		if(singleton == null) {
			try {
				singleton = new DatabaseConnection();
			} catch (Exception e) {
				System.out.println("Failed to connect to database");
				e.printStackTrace();
			}
		}
		return singleton;
	}
	
	public java.sql.PreparedStatement prepareStatement(String query) {
		try {
			return connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public DatabaseMetaData getMetaData() {
		try {
			return connection.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
