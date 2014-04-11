package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBaseConn {

	
	private DataBaseConn(){
		
	}
	
	public static Connection getConnection(String sid, String host,String port,String userName, String password) throws SQLException{
		Connection conn = null;
		String connString = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;		
		conn = DriverManager.getConnection(connString, userName, password);
		return conn;
	}
	
	public static void closeConnection(Connection conn) throws SQLException{
		conn.close();
	}
	
	
}
