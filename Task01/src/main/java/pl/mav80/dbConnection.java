package pl.mav80;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {

	Connection dbConnection = null;

	//connection obtain method
	public Connection getConnection() {
		try {
			this.dbConnection =  DriverManager.getConnection("jdbc:hsqldb:mem:orders", "root", "root");
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return this.dbConnection;
	}

	//close connection method
	public void closeConnection() {
		if(this.dbConnection != null) {
			try {
				this.dbConnection.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
