package com.automation.toolbox.database.connectionManger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class DbCheckPoint {

	public DbCheckPoint() {
	}

	

	public static boolean check(String connectionString, String SQL, int MaxPollTimeoutInMins, int pollIntervalInSecs, String value) {
		
		int totalSec = MaxPollTimeoutInMins * 60;
		int iteration = totalSec / pollIntervalInSecs;
		
		DbReadData dbread = new DbReadData(connectionString);
		for (int i = 0; i <= iteration; i++) {
			ResultSet rs = dbread.getResultSet(SQL);
			try {
				String output = "";
				while (rs.next()) {
					output = rs.getString(1);
					break;
				}
				if (value.equals(output)) {
					
					return true;
				} else {
					Thread.sleep(pollIntervalInSecs * 1000);
					continue;
				}

			} catch (SQLException | InterruptedException e) {
				e.printStackTrace();
			} 
		}
		return false;

	}

	public static boolean check(String connectionString, String SQL, int MaxPollTimeoutInMins, int pollIntervalInSecs, List<String> valueArray) {

		int totalSec = MaxPollTimeoutInMins * 60;
		int iteration = totalSec / pollIntervalInSecs;

		DbReadData dbread = new DbReadData(connectionString);
		for (int i = 0; i <= iteration; i++) {

			ResultSet rs = dbread.getResultSet(SQL);

			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();

				List<String> columnValues = new ArrayList<String>();
				while (rs.next()) {
					for (int j = 1; j <= columnCount; j++) {
						columnValues.add(rs.getString(j));
					}
					break;
				}

				if (valueArray.equals(columnValues)) {
					return true;
				} else {
					Thread.sleep(pollIntervalInSecs * 1000);
					continue;
				}

			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		return false;
	}

	public static boolean check(String connectionString, String SQL, int MaxPollTimeoutInMins, int pollIntervalInSecs, LinkedHashMap<String, String> columnValueMap) {

		int totalSec = MaxPollTimeoutInMins * 60;
		int iteration = totalSec / pollIntervalInSecs;
		boolean flagMatched = true; 
		LinkedHashMap<String, String> outputColumnValueMap = null; 
		DbReadData dbread = new DbReadData(connectionString);
		for (int i = 0; i <= iteration; i++) {

			ResultSet rs = dbread.getResultSet( SQL);

			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();

				outputColumnValueMap = new LinkedHashMap<String, String>();

				while (rs.next()) {
					for (int j = 1; j <= columnCount; j++) {
						String colName = rsmd.getColumnName(j);
						String value = rs.getString(j);
						outputColumnValueMap.put(colName, value);
					}
					break; // read only first row
				}

				Set<String> keySet = columnValueMap.keySet();
				
				for (String string : keySet) {
					if (outputColumnValueMap.containsKey(string)) {
						if (!outputColumnValueMap.get(string).equals(columnValueMap.get(string))) {
							flagMatched = false; 
							break;
						}

					} else {
						flagMatched = false; 
						break;
					}
				}
				if(!flagMatched){
					Thread.sleep(pollIntervalInSecs * 1000);
					continue; 
					
				}else {
					return true;	
				}
				

			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(outputColumnValueMap);
		return false;

	}

}
