package com.automation.toolbox.database.connectionManger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

	private java.sql.Connection conn;

	public DbConnection(java.sql.Connection conn) {
		this.conn = conn;
	}

	public java.sql.Connection getInstance() {
		return conn;
	}
	
	public void Close(){
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("connection is already closed");
		}
	}

	public ResultSet executeSQL(String sql) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			return stmt.executeQuery(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public int executeUpdate(String sql) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
	}

	public boolean isConnected() {

		if (this.conn != null) {
			try {
				if (!this.conn.isClosed()) {
					if (this.conn.isValid(0)) {
						return true;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;

	}

}
