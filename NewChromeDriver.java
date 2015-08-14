package newselenium;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class NewChromeDriver extends ChromeDriver implements NewWebDriver {

	public boolean exists(By by, int timeoutInSec) {
		boolean test;
		this.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		test = !this.findElements(by).isEmpty();
		this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		return test;
	}

	public boolean existsAndDisplayed(By by, int timeoutInSec) {
		// TODO Auto-generated method stub
		if (exists(by, timeoutInSec)) {
			return this.findElement(by).isDisplayed();
		} else {
			return false;
		}
	}

	
	public NewChromeDriver() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NewChromeDriver(Capabilities capabilities) {
		super(capabilities);
		// TODO Auto-generated constructor stub
		
	}

	public NewChromeDriver(ChromeDriverService service, Capabilities capabilities) {
		super(service, capabilities);
		// TODO Auto-generated constructor stub
	}

	public NewChromeDriver(ChromeDriverService service, ChromeOptions options) {
		super(service, options);
		// TODO Auto-generated constructor stub
	}

	public NewChromeDriver(ChromeDriverService service) {
		super(service);
		// TODO Auto-generated constructor stub
	}

	public NewChromeDriver(ChromeOptions options) {
		super(options);
		//System.out.println(super);
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	public boolean waitforExists(By by, int timeoutInSec) {
		// TODO Auto-generated method stub
		int startcount = 0;
		this.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		for (int i = 0; i <= timeoutInSec; i++) {
			if (!this.findElements(by).isEmpty()) {
				if (this.findElements(by).get(0).isDisplayed()) {
					this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
					return true;
				}
			}
			if (startcount > timeoutInSec) {
				this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				return false;
			}
			try {
				Thread.sleep(1000);
				startcount = startcount + 1;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		return false;
	}

	@Override
	public boolean waitTillExists(By by, int timeoutInSec) {
		// TODO Auto-generated method stub

		int startcount = 0;
		this.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		for (int i = 0; i <= timeoutInSec; i++) {
			try {

				if (!this.findElements(by).isEmpty()) {
					if (!this.findElements(by).get(0).isDisplayed()) {
						this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						return true;
					}
				} else {
					return true;
				}
			} catch (Exception e) {
				// TODO: handle exception
				continue;
			}

			if (startcount > timeoutInSec) {
				this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				return false;
			}
			try {
				Thread.sleep(1000);
				// System.out.println("syncing...");
				startcount = startcount + 1;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		return false;
	}

	@Override
	public HashMap<String, WebElement> eitherOfExists(LinkedHashMap<String, By> objectList, int timeoutInSec) {
		// TODO Auto-generated method stub
		this.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		int startcount = 0;

		for (int i = 0; i <= timeoutInSec; i++) {
			for (String objName : objectList.keySet()) {
				if (this.findElements(objectList.get(objName)).size() > 0) {
					if (this.findElements(objectList.get(objName)).get(0).isDisplayed()) {
						WebElement obj = this.findElements(objectList.get(objName)).get(0);
						HashMap<String, WebElement> map = new HashMap<String, WebElement>();
						map.put(objName, obj);
						this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						return map;
					}
				}
				if (startcount > timeoutInSec) {
					this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
					return null;
				}

				try {
					Thread.sleep(1000);
					startcount = startcount + 1;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		return null;
	}

	@Override
	public WebElement $x(String xpath) {
		// TODO Auto-generated method stub
		List<WebElement> els = this.findElements(By.xpath(xpath));
		if (els.size() >= 0) {
			return els.get(0);
		}
		return null;
	}

	public boolean isLoadedSuccessfully(By element) {
		this.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		By errorMessage = By.xpath("//div[contains(text(),'Error code: ERR_CONNECTION_REFUSED')]");

		for (int i = 0; i < 60; i++) {

			if (!this.findElements(errorMessage).isEmpty()) {
				return false;
			}

			if (!this.findElements(element).isEmpty()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		return false;
	}

	@Override
	public boolean waitProperty(By by, String attribute, String regExValue, int timeoutInSec) {
		
		long startTime = System.currentTimeMillis();
		
		// TODO Auto-generated method stub
		if (waitforExists(by, timeoutInSec)) {
			long estimatedTime = System.currentTimeMillis() - startTime;
			int secleft = (int) (estimatedTime / 1000); 
			
			int counter = 1;
			String attribValue=""; 
			
			if(attribute.toLowerCase().equals("text")){
				attribValue = this.findElement(by).getText();
			}
			else {
				attribValue = this.findElement(by).getAttribute(attribute);	
			}
			
			while (!attribValue.equals(regExValue) && counter < secleft) {
				counter++;
				if(attribute.toLowerCase().equals("text")){
					attribValue = this.findElement(by).getText();
				}
				else {
					attribValue = this.findElement(by).getAttribute(attribute);	
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (attribValue.matches(regExValue)) {
				return true;
			}

		} else {
			return false;
		}
		return false;
	}

}
