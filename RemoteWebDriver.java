package newselenium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ReusableRemoteWebDriver extends RemoteWebDriver implements NewWebDriver {

	private static final String SID_FILE = System.getProperty("java.io.tmpdir") + "seleniumSession.txt";;

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

	public ReusableRemoteWebDriver(SupportedBrowsers browsers, DesiredCapabilities capabilities, boolean NewSession) {
		String sessionData = null;
		String address = "";
		if(!NewSession){
			sessionData = readPreviousSessionId();	
		}
		if (sessionData != null) {
			String hostport = sessionData.split("\\|")[0];
			address=hostport;
			String SessionIdStirng = sessionData.split("\\|")[1];
			URL url = null;
			try {
				url = new URL(hostport);
			} catch (MalformedURLException e) {
			}
			CommandExecutor executor = new HttpCommandExecutor(url);
			setCommandExecutor(executor);
			setSessionId(SessionIdStirng);

			try {
				System.out.println(getCurrentUrl());
			} catch (Exception ex) {
				try {
					startSession(capabilities);
					saveSessionId(address + "|" + getSessionId().toString());
				} catch (Exception ex1) {
					//System.out.println(ex1.getMessage());
					address = startServer(browsers);
					startSession(capabilities);
					saveSessionId(address + "|" + getSessionId().toString());
				}
			}

		} else {
			address = startServer(browsers);
			startSession(capabilities);
			saveSessionId(address + "|" + getSessionId().toString());
		}
	}

	private String startServer(SupportedBrowsers browsers) {
		int port = PortFinder.findFreePort();
		System.out.println();

		Runtime rt = Runtime.getRuntime();
		try {
			if(browsers== SupportedBrowsers.Chrome){
				rt.exec(new String[] { "chromedriver32.exe", "--port=" + port });
			}
			else if(browsers== SupportedBrowsers.InternetExplorer){
				rt.exec(new String[] { "IEDriverServer32.exe", "/port=" + port });
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String address = "http://localhost:" + port;
		URL url = null;
		try {
			url = new URL(address);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CommandExecutor executor = new HttpCommandExecutor(url);
		setCommandExecutor(executor);
		return address;

	}

	private String readPreviousSessionId() {
		String sid = null;
		File sidFile = new File(SID_FILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sidFile));
			sid = reader.readLine();
		} catch (IOException e) {
			// noop
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sid;
	}

	private void saveSessionId(String sid) {
		File sidFile = new File(SID_FILE);
		FileWriter writer = null;
		try {
			writer = new FileWriter(sidFile);
			writer.write(sid);
		} catch (IOException e) {
			// noop
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	void setReusedCapabilities(Capabilities capabilities) {
		Field capabilitiesField = getFieldSafely(this, RemoteWebDriver.class, "capabilities");
		writeValueToField(this, capabilitiesField, capabilities);
	}

	private static Field getFieldSafely(Object object, Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static void writeValueToField(Object object, Field field, Object value) {
		boolean wasAccessible = field.isAccessible();
		if (!wasAccessible) {
			field.setAccessible(true);
		}
		try {
			field.set(object, value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (!wasAccessible) {
			field.setAccessible(false);
		}
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
			String attribValue = "";

			if (attribute.toLowerCase().equals("text")) {
				attribValue = this.findElement(by).getText();
			} else {
				attribValue = this.findElement(by).getAttribute(attribute);
			}

			while (!attribValue.equals(regExValue) && counter < secleft) {
				counter++;
				if (attribute.toLowerCase().equals("text")) {
					attribValue = this.findElement(by).getText();
				} else {
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
