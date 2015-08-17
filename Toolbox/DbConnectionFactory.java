package com.automation.toolbox.database.connectionManger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionFactory {

	private String databaseURL = "";
	
	
	public DbConnectionFactory(String databaseURL) {
		this.databaseURL = databaseURL;
	}
	

	
	// build a new connection
	public com.clsa.automation.toolbox.database.connectionManger.DbConnection buildConnection(DataBaseType databaseType) {
		
		Driver myDriver = getDriver(databaseType);
		if(myDriver==null){
			throw new RuntimeException("Database Type can not be null");
		}
		try {
			DriverManager.registerDriver(myDriver);
			Connection conn = DriverManager.getConnection(databaseURL);
			return new com.clsa.automation.toolbox.database.connectionManger.DbConnection(conn);
			// System.out.println(x);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error Connecting Database, Please Check Credential or Database Env Properties");
			e.printStackTrace();
			return null;
		}

	}
	
	public Driver getDriver(DataBaseType databaseType){
		switch (databaseType) {
		case ORACLE:
			return new oracle.jdbc.driver.OracleDriver();
		case SQLSERVER:
			return new net.sourceforge.jtds.jdbc.Driver();
		default:
			return null; 
		}
		
		
		
	}
	
	

}
