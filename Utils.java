package Common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

	public static String Today(String format) {
		SimpleDateFormat sdt = new SimpleDateFormat(format);
		Date date = new Date();
		return sdt.format(date);
	}

	
	public static String generate9DigitUniqueId(){
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		String dt =  Utils.Today("yyMMddHHmmss");
		dt = dt.substring(1, dt.length());
		String year = dt.charAt(0) + "";  
		String _month = dt.charAt(1) + ""+  dt.charAt(2);  
		String month = alpha.charAt(Integer.parseInt(_month) -1) + "";
		String date = dt.charAt(3) + ""+  dt.charAt(4);
		String _hr = dt.charAt(5) + ""+  dt.charAt(6);
		String hr = alpha.charAt(Integer.parseInt(_hr) -1) + "";
		String minsec = dt.substring(7, dt.length());
		
		String full = year + month + date + hr + minsec; 
		return full; 
		
	}
	
	public static String NextBusinessDate(String market, int addTPlusDays) {
		return NextBusinessDate(market, addTPlusDays, "");
	}

	public static String NextBusinessDate(String market, int addTPlusDays, String StartYYYYMMDD) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		if (StartYYYYMMDD == "") {
			c.setTime(new Date());
		} else {
			Date date;
			try {
				date = sdf.parse(StartYYYYMMDD);
				c.setTime(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		int daysToAdd = addTPlusDays;
		boolean daysAdded = false;
		Calendar newC = c;

		while (daysToAdd > 0) {
			while(!daysAdded){
				newC.add(Calendar.DATE, 1);	
				if(!isWeekend(newC) && !isHoliday(newC, market)){
					daysAdded =true;
				}
			}
			
			daysAdded = false; 
			daysToAdd--; 
		}
		return sdf.format(newC.getTime());
	}

	private static boolean isWeekend(Calendar c) {
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			return true; 
		}
		return false;
	}

	private static boolean isHoliday(Calendar c, String market) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> holidays = getHolidayList(market);
		if(holidays.contains(sdf.format(c.getTime()))){
			return true; 
		}
		return false;
	}

	

	private static List<String> getHolidayList(String market) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("Holidays/holidays.csv"));
			String newline;
			boolean started = false;
			List<String> holidayList = new ArrayList<String>();
			while ((newline = reader.readLine()) != null) {
				newline = newline.replace("\"", "");
				String[] arr = newline.split(",");
				if (arr[0].equals(market)) {
					started = true;
					holidayList.add(arr[1]);
				} else if (started) {
					reader.close();
					break;
				}
			}
			reader.close();
			return holidayList;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Object coalesce(Object checkvalue, Object elsevalue){
//		return checkvalue !=null ? checkvalue : elsevalue; 
		if (checkvalue==null){
			return elsevalue;
		}else{
			if (checkvalue.equals("")){
				return elsevalue;
			}else{
				return checkvalue;
			}
		}
	}
	

	

}
