package websyn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import newselenium.NewWebDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class WebTable {

	WebElement tableHeader = null;
	WebElement tableContents = null;
	String xpath = null;
	NewWebDriver driver = null;
	int tries = 0;

	public WebTable(NewWebDriver driver, String xpath, WebElement tableHeader) {
		// TODO Auto-generated constructor stub
		this.tableHeader = tableHeader;
		this.driver = driver;
		this.xpath = xpath;
	}

	
	public WebTable(NewWebDriver driver, String xpath, WebElement tableHeader, WebElement tableContents) {
		// TODO Auto-generated constructor stub
		this.tableHeader = tableHeader;
		this.tableContents = tableContents;
		this.driver = driver;
		this.xpath = xpath;
	}

	public void rebuildElement() {
		List<WebElement> ctrls = driver.findElements(By.xpath(xpath));
		if (ctrls.size() == 2) {
			tableHeader = ctrls.get(0);
			tableContents = ctrls.get(1);
		} else if (ctrls.size() == 1) {
			tableHeader = ctrls.get(0);
		}
	}

	public int getRowCount() {
		try {
			if (tableContents == null) {
				return 0;
			} else {
				return tableContents.findElements(By.tagName("tr")).size();
			}
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getRowCount();
		}

	}

	// ccm messages fix dev sachine kumar

	public int getColumnCount() {
		try {
			int colcount = tableHeader.findElements(By.tagName("th")).size();
			if (colcount == 0) {
				colcount = tableHeader.findElements(By.tagName("td")).size();
			}
			return colcount;
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getRowCount();
		}
	}

	public WebElement getRowObject(int rowindex) {
		try {
			List<WebElement> rows = tableContents.findElements(By.tagName("tr"));
			if (rows.size() > rowindex) {
				return rows.get(rowindex);
			} else {
				return null;
			}
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getRowObject(rowindex);
		}
	}
	
	

	public WebElement getRowObject(String colDisplayText, String uniqueValue){
		int colIndex = getColIndexfromDisplayHeader(colDisplayText);
		for (int i = 0; i < getRowCount(); i++) {
			if(uniqueValue.equals(getCellValue(i, colIndex))){
				return getRowObject(i);
			} 
		}
		return null;
	}
	
	public int getRowIndex(String colDisplayText, String uniqueValue){
		int colIndex = getColIndexfromDisplayHeader(colDisplayText);
		for (int i = 0; i < getRowCount(); i++) {
			if(uniqueValue.equals(getCellValue(i, colIndex))){
				return i;
			} 
		}
		return -1;
	}
	
	public int getColIndexfromDisplayHeader(String colDisplayText) {
		try {
			List<WebElement> cols = tableHeader.findElements(By.tagName("th"));
			int i = 0;
			boolean colfound = false;
			for (WebElement col : cols) {
				if (colDisplayText.equals(col.getAttribute("textContent"))) {
					colfound = true;
					break;
				} else {
					i++;
				}
			}
			if (colfound) {
				return i;
			} else {
				return -1;
			}
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getColIndexfromDisplayHeader(colDisplayText);
		}
	}

	public WebElement getCellObject(int rowindex, int colIndex) {
		try {
			WebElement row = getRowObject(rowindex);
			
			
			if (row != null) {
				int colCount = getColumnCount();
				if (colCount > colIndex) {
					return row.findElements(By.tagName("td")).get(colIndex);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getCellObject(rowindex, colIndex);
		}
	}
	
	
	

	public WebElement getCellObject(int rowindex, String colDisplayText) {
		try {
			int colIndex = getColIndexfromDisplayHeader(colDisplayText);
			if (colIndex >= 0) {
				return getCellObject(rowindex, colIndex);
			} else {
				return null;
			}
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getCellObject(rowindex, colDisplayText);
		}
	}
	
	public WebElement getControlInsideCell(int rowindex, String colDisplayText, String tagName ){
		// error here 
		WebElement cellObject = getCellObject(rowindex,colDisplayText);
		List<WebElement> elements = cellObject.findElements(By.tagName(tagName));
		if(elements.size() >0){
			WebElement control = elements.get(0);
			return control; 
		}
		return null; 
	}
	

	public String getCellValue(int rowindex, int colIndex) {
		try {
			WebElement cell = getCellObject(rowindex, colIndex);
			if (cell != null) {
				return cell.getAttribute("textContent");
			}
			System.out.println("Error:CouldNotFoundCellObject");
			return "Error:CouldNotFoundCellObject";
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getCellValue(rowindex, colIndex);
		}
	}

	public String getCellValue(int rowindex, String colDisplayText) {
		try {
			WebElement cell = getCellObject(rowindex, colDisplayText);
			if (cell != null) {
				return cell.getAttribute("textContent");
			}
			System.out.println("Error:CouldNotFoundCellObject");
			return "Error:CouldNotFoundCellObject";
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			return getCellValue(rowindex, colDisplayText);
		}
	}

	public void rowClick(int rowindex) {
		try {
			getRowObject(rowindex).click();
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			rowClick(rowindex);
		}
	}

	public void rowDoubleClick(int rowindex) {
		try {
			Actions action = new Actions(driver);
			WebElement elm = getRowObject(rowindex).findElements(By.tagName("td")).get(0); 
			action.doubleClick(elm).perform();
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			rowDoubleClick(rowindex);
		}
	}

	public void rowRightClick(int rowindex) {
		try {
			Actions action = new Actions(driver);
			action.contextClick(getRowObject(rowindex)).perform();
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			rowRightClick(rowindex);
		}
	}

	public void rowHover(int rowindex) {
		try {
			Actions action = new Actions(driver);
			action.moveToElement(getRowObject(rowindex)).perform();
		} catch (StaleElementReferenceException e) {
			rebuildElement();
			rowHover(rowindex);
		}
	}

	public List<String> getColumnNames() {
		List<WebElement> cols = tableHeader.findElements(By.tagName("th"));
		List<String> colNames  = new ArrayList<String>();
		for (WebElement col : cols) {
			colNames.add(col.getAttribute("textContent"));
		}	
		return colNames;
	}
	
	public HashMap<String, String> getRow(int rowindex){
		HashMap<String, String> row = new HashMap<String, String>(); 
		int cols= getColumnCount(); 
		List<String> colNames = getColumnNames();
		
		for (int i = 0; i < cols; i++) {
			String colName = colNames.get(i);
			String value = getCellValue(rowindex,i);
			row.put(colName, value);
		}
		return row;
	}
	
	
	public boolean isRowExist(HashMap<String , String> row){
		int rowCount = getRowCount();
		int colcount = row.size();
		
		for (int i = 0; i < rowCount; i++) {			
			HashMap<String, String> tablerow = getRow(i);
			int x=0;
			for (String key : row.keySet()) {
				x++;
				if(!tablerow.containsKey(key)){
					return false;
				}
				if(tablerow.get(key).equals(row.get(key))){					
					if(x==colcount){
						return true;
					}
					continue;
				}
				else {
					break;
				}
			}
		}
		return false;
	}

	
}
