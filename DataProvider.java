package webtesting;

import org.ini4j.Profile.Section;

import Common.Utils;

import com.clsa.automation.toolbox.utilities.IniReader;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class DataProvider {
	Section sec;

	public DataProvider(String DataType, String testDataFileName, String sectionName) {
		IniReader ini = new IniReader("TestData/" + testDataFileName);
		sec = ini.getIniSection(sectionName);
	}

	public DataProvider(String DataType, String testDataFileName, String sectionName, String flag) {
		IniReader ini = new IniReader("TestData/" + testDataFileName);
		sec = ini.getIniSection(sectionName + " flag=" + flag);
	}

	public String read(String key) {
		if (sec.containsKey(key)) {
			return processKeyword(sec.fetch(key));
		} else {
			throw new UncheckedExecutionException("\n DataProvider Exception : key not found \n " + "KeyName : " + key + "|  Section Name : " + sec.getName() + "\n\n", null);
		}
	}

	public String read(String key, int index) {
		if (sec.containsKey(key)) {
			return processKeyword(sec.fetch(key, index));
		} else {
			throw new UncheckedExecutionException("\n DataProvider Exception : key not found \n " + "KeyName : " + key + "|  Section Name : " + sec.getName() + "\n\n", null);
		}
	}

	public Section getSection() {
		return sec;
	}


	// {TODAY:dd/MM/yyyy}
	public static String processKeyword(String keyword) {
		if (keyword.contains("{") && keyword.contains("}")) {

			keyword = keyword.replace("{", "").replace("}", "");
			String[] arr = keyword.split(":");
			String param = "";
			if (arr.length > 1) {
				param = arr[1];
			}
			switch (arr[0]) {
			case "TODAY":
				return Utils.Today(param);
			default:
				return "";
			}
		}
		return keyword; 
	}
}
