package webtesting;

import java.io.IOException;

import newselenium.NewWebDriver;
import newselenium.ScreenShot;
import newselenium.SupportedBrowsers;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@Listeners(ScreenShot.class)
public class TestCase {
	public NewWebDriver driver = null;
	public DataProvider dp = null;
	public boolean newSession = true;
	public String LauchBrowserYesNo = "yes";
	public String TestDataFileName = "";
	private ITestContext context = null;
	
	private SupportedBrowsers supportedBrowsers = SupportedBrowsers.Chrome;
	
	
	public TestCase() {

	}

	public TestCase(NewWebDriver driver) {
		this.driver = driver;
	}

	public TestCase(String LauchBrowserYesNo) {
		this.LauchBrowserYesNo = LauchBrowserYesNo.toLowerCase();
	}

	public TestCase(boolean newBrowserSession) {
		this.newSession = newBrowserSession;
	}

	public TestCase(SupportedBrowsers supportedBrowsers) {
		this.supportedBrowsers = supportedBrowsers;
	}

	public void LauchBrowser() {
		if (driver == null) {
			driver = Driver.launch(supportedBrowsers, newSession);
		}
		driver.manage().window().maximize();
		context.setAttribute("Driver", driver);
	}

	public DataProvider creatDataProvider(String TestDataFileName) {
		return new DataProvider("ini", TestDataFileName, this.getClass().getName().split("\\.")[1].toString());
	}

	@Parameters({ "TestDataFileName"})
	@BeforeClass(alwaysRun = true)
	public void setupBeforeClass(ITestContext context, @Optional("TestData.ini") String TestDataFileName) {
		if (this.TestDataFileName == "") {
			this.TestDataFileName = TestDataFileName;
		}
		System.out.println("Starting Execution with Data File : " + this.TestDataFileName);
		dp = new DataProvider("ini", this.TestDataFileName, this.getClass().getName().split("\\.")[1].toString());

		if (LauchBrowserYesNo.equals("yes")) {
			if (driver == null) {
				driver = Driver.launch(supportedBrowsers, newSession);
			}
			driver.manage().window().maximize();
			context.setAttribute("Driver", driver);
		}
		this.context = context;
	}

	@AfterClass
	public void disposeDriver() {
		
		try {
			if (newSession && driver != null) {
				driver.quit();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}	
	
	@BeforeSuite
	@AfterSuite
	public void killBrowsers(){
		Runtime rt = Runtime.getRuntime();
		try {
			if (newSession) {
				rt.exec("taskkill /F /IM chrome.exe");
				rt.exec("taskkill /F /IM chromedriver32.exe");	
				
				rt.exec("taskkill /F /IM iexplore.exe");
				rt.exec("taskkill /F /IM IEDriverServer32.exe");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
