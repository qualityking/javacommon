package webtesting;

import java.util.concurrent.TimeUnit;

import newselenium.NewWebDriver;
import newselenium.ReusableRemoteWebDriver;
import newselenium.SupportedBrowsers;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Driver {
	
	public static NewWebDriver driver;
	
	public Driver() {
		// TODO Auto-generated constructor stub
	}

	
	public static NewWebDriver launch(SupportedBrowsers BrowserType, boolean newSession) {
		NewWebDriver driver = null;
		DesiredCapabilities dcap;

		switch (BrowserType) {
		case InternetExplorer:
			System.setProperty("webdriver.ie.driver", "IEDriverServer32.exe");
			dcap = DesiredCapabilities.internetExplorer();
			dcap.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, false);
			dcap.setCapability(CapabilityType.SUPPORTS_APPLICATION_CACHE, true);
			//driver = new NewInternetExplorerDriver((Capabilities) dcap);
			driver  = new ReusableRemoteWebDriver(SupportedBrowsers.InternetExplorer, dcap , newSession);
			break;
		case Chrome:
			System.setProperty("webdriver.chrome.driver", "chromedriver32.exe");
			
			ChromeOptions options = new ChromeOptions();
			
			String username = System.getProperty("user.name");
			String cacheDir =  "C:/Users/" + username + "/AppData/Local/Google/Chrome/User Data/Default/Cache";
			options.addArguments("disk-cache-dir=" + cacheDir);
			
			options.addArguments("--disable-extensions");
			//driver = new NewChromeDriver(options);
			
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			
			
			
			driver  = new ReusableRemoteWebDriver(SupportedBrowsers.Chrome, capabilities, newSession);
			//driver  = new ReusableRemoteWebDriver(url);

			break;
		case FireFox:
			try {
				throw new Exception("Browser is not supported yet : FireFox");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		}

		driver.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		System.out.println(driver.getSessionId());
		return driver;
		
	}

	public static void threadSleep(long millisecond) {

		try {
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
