package com.oultoncollege;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

/**
 * TipCalculatorActivityTestUI
 * 	Appium example for Automated Testing both a Mobile App and Mobile WebApp remotely through a 
 *  "black-box" test.
 * 	
 * PREREQUISITES FOR TESTS TO PASS:
 * 	- ANDROID - Lab3 completed and running on a Device or Emulator that you point to using DeviceName
 *  For iOS on Mac:
 *		brew install ios-webkit-debug-proxy
 *      brew install libimobiledevice
 *		brew install usbmuxd
 *		brew install carthage
 *		npm install -g ios-deploy
 *		ios_webkit_debug_proxy -c <UDID>:27753
 * 
 * @author bcopeland
 * @version 1.0.0
 * @since 2020-02-13
 */
@Tag("mobile-app-webapp")
@TestInstance(Lifecycle.PER_CLASS)
public class AppiumTestiOS {
	
	private AppiumDriverLocalService appiumService;
	private AppiumDriver<MobileElement> appiumDriver;
	private WebDriver seleniumDriver;
	
	private static final String NODEJS_HOME = "/usr/local/bin/node";
//	private static final String NODEJS_HOME = "C:\\APPS\\NodeJS\\node.exe";
	private static final String APPIUM_HOME = "/usr/local/bin/appium";
//	private static final String APPIUM_HOME = "C:\\APPS\\Appium\\";
	private static final int APPIUM_PORT = 4723;
	private static final String APPIUM_URL = "http://"+TestUtils.getIP()+":"+APPIUM_PORT+"/wd/hub"; //Appium Server path (default local, but could be remote or cloud)
	
	private static final String DRIVER_PATH = "/usr/bin/safaridriver"; //iOS+Safari - Mac
//	private static final String DRIVER_PATH = "/opt/TestAutomation/Selenium/WebDrivers/Chrome/chromedriver_mac/chromedriver"; //Android+Chrome - Mac
//	private static final String DRIVER_PATH = "/opt/TestAutomation/Selenium/WebDrivers/FireFox/geckodriver-v0.26.0-win64//geckodriver.exe"; //Android+FF - Windows
//	private static final String DRIVER_PATH = "C:\\APPS\\TestAutomation\\Selenium\\WebDrivers\\Chrome\\chromedriver_win32\\chromedriver.exe"; //Android+Chrome - Windows

	private static final String BROWSER_NAME = "Safari"; //"Firefox"   "Chrome"
	private static final String MOBILE_OS = "iOS"; //"Android"
	private static final String MOBILE_OS_VERSION = "13.3"; //"4.4.2"   "7.0"   "8.0"  "9.0"  "12.0" etc
	private static final String DEVICE_NAME = "iPhone 11 Pro Max"; //change to match your DeviceName, i.e. "Galaxy Nexus API 28"
	private static final String DEVICE_UDID = "2350437B-D31B-4C28-994A-509A4A14B976"; //should match your actual Device UDID
	
	//App
	private static final String APP_PACKAGE = "com.oultoncollege.pointofsale"; //package name of App under test
	private static final String APP_MAIN_ACTIVITY = "PointOfSaleActivity"; //main activity class of App under test

	

	@BeforeAll
	public void startAppiumServer() throws IOException {
	    String osName = System.getProperty("os.name");
	    if (osName.contains("Mac")) {
	        appiumService = AppiumDriverLocalService.buildService(new 
	        AppiumServiceBuilder()
                .usingDriverExecutable(new File("/usr/local/bin/node"))
                .withAppiumJS(new File("/usr/local/bin/appium"))
                .withIPAddress(TestUtils.getIP())
                .usingPort(APPIUM_PORT)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withLogFile(new File("build/appium.log")));
	    } else if (osName.contains("Windows")) {
		    appiumService = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
		            .usingDriverExecutable(new File((NODEJS_HOME)))
		            .withAppiumJS(new File((APPIUM_HOME)))        
		            .withIPAddress(TestUtils.getIP())
		            .usingPort(APPIUM_PORT)
		            .withArgument(GeneralServerFlag.SESSION_OVERRIDE) 
		            .withLogFile(new File(APPIUM_HOME+"\\appium_"+LocalDateTime.now()+".log")));
	    } else {
	    	//assume Linux
	    	
	    }
	    appiumService.start();
	}

	
	@BeforeEach
	public void setUp() throws MalformedURLException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("device", MOBILE_OS);
			capabilities.setCapability(CapabilityType.VERSION, MOBILE_OS_VERSION);
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, DEVICE_NAME);
//			capabilities.setCapability(MobileCapabilityType.UDID, DEVICE_UDID); //iOS only
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
			DesiredCapabilities webCapabilities = new DesiredCapabilities(capabilities); // initialize web capabilities to common required set 
		// App
			capabilities.setCapability("appPackage", APP_PACKAGE); // setup desired capabilities and pass the Android app-activity and app-package to Appium
			capabilities.setCapability("appActivity", APP_PACKAGE + "." + APP_MAIN_ACTIVITY); // package name of our app (can get it from "APK info" app or "Android Device Monitor" on Android)
		appiumDriver = new AppiumDriver<MobileElement>(new URL(APPIUM_URL), capabilities);

		// WebApp
			webCapabilities.setCapability("device", MOBILE_OS);
			webCapabilities.setCapability(CapabilityType.VERSION, MOBILE_OS_VERSION);
			webCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, DEVICE_NAME);
//			webCapabilities.setCapability(MobileCapabilityType.UDID, DEVICE_UDID); //iOS only
			webCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
			webCapabilities.setCapability(CapabilityType.BROWSER_NAME, BROWSER_NAME);
			webCapabilities.setCapability("chromedriverExecutable", DRIVER_PATH); //must point to your local Chrome driver or it likely won't work
			webCapabilities.setCapability("unicodekeyboard", true);
		seleniumDriver = new RemoteWebDriver(new URL(APPIUM_URL), webCapabilities); // launch the Calculator App in Android Device using the configurations specified in Desired Capabilities
	}
	
	
	/* Mobile App test example */
	@Test
	public void testAppCalculator() throws Exception {
//		IOSElement billAmount = (IOSElement) new WebDriverWait(appiumDriver, 30).until(ExpectedConditions.elementToBeClickable(MobileBy.id("billAmountEditText")));		
//		IOSElement billAmount = (IOSElement) new WebDriverWait(appiumDriver, 30).until(ExpectedConditions.visibilityOfElementLocated(MobileBy.id(APP_PACKAGE+":id/billAmountEditText")));
		IOSElement billAmount = (IOSElement) appiumDriver.findElement(MobileBy.id("billAmountEditText"));
//		IOSElement billAmount = (IOSElement) appiumDriver.findElementById(APP_PACKAGE+":id/billAmountEditText");
//		List<WebElement> editText = appiumDriver.findElements(By.className("android.widget.EditText"));
//		IOSElement billAmount = (IOSElement) editText.get(0);
		billAmount.clear();
		billAmount.setValue("50");
		
//		IOSElement calculateButton = (IOSElement) appiumDriver.findElement(MobileBy.id("calculateButton"));
//		calculateButton.click();
		
		final String EXPECTED_RESULT = "$57.50";
		IOSElement totalAmount = (IOSElement) appiumDriver.findElement(MobileBy.id("totalTextView"));
		assertEquals(EXPECTED_RESULT, totalAmount.getText());
	}


	/* Mobile Web test example */
	@Test
	public void testWebCalculator() throws Exception {
		seleniumDriver.get("https://www.canada.ca/en/revenue-agency/services/tax/businesses/topics/gst-hst-businesses/charge-collect-which-rate/calculator.html");
		seleniumDriver.findElement(By.id("ProvOrTerr")).click();
		WebElement dropdown = seleniumDriver.findElement(By.id("ProvOrTerr"));
        dropdown.findElement(By.xpath("//option[. = 'New Brunswick']")).click();
        seleniumDriver.findElement(By.cssSelector("option:nth-child(5)")).click();
        seleniumDriver.findElement(By.id("priceinput")).click();
        seleniumDriver.findElement(By.id("priceinput")).sendKeys("100");
//        seleniumDriver.findElement(By.xpath("//label[contains(.,'Before taxes')]")).click();
//        seleniumDriver.findElement(By.xpath("(//input[@name=\'BefOrAft\'])[0]")).click();
        seleniumDriver.findElements(By.cssSelector("input[type=radio]"));
        seleniumDriver.findElement(By.name("BefOrAft")).click();
        seleniumDriver.findElement(By.id("calculate")).click();
        final String EXPECTED_RESULT = "$115.00";
	    assertEquals(EXPECTED_RESULT, seleniumDriver.findElement(By.id("Tot")).getText());		
	}
	
	
	public static void main(String[] args) {
		AppiumTestiOS tc = new AppiumTestiOS();

		//Create Appium WebDriver instance to connect to the Appium server
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			//Set up desired capabilities and pass the Android app-activity and app-package to Appium
			capabilities.setCapability("device", MOBILE_OS);
			capabilities.setCapability(CapabilityType.VERSION, MOBILE_OS_VERSION);
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, DEVICE_NAME);
//			capabilities.setCapability(MobileCapabilityType.UDID, DEVICE_UDID);
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
				capabilities.setCapability("appPackage", APP_PACKAGE); // package name of our app (can get it from "APK info" app or "Android Device Monitor" on Android)
				capabilities.setCapability("appActivity", APP_PACKAGE + "." + APP_MAIN_ACTIVITY); // This is Launcher activity of your app (you can get it from apk info app)
				
		   //Test#1 - App
			tc.appiumDriver = new AppiumDriver<MobileElement>(new URL(APPIUM_URL), capabilities);
			tc.testAppCalculator();

			
			capabilities = new DesiredCapabilities(); //reset capabilities for Mobile Web
			capabilities.setCapability("device", MOBILE_OS);
			capabilities.setCapability(CapabilityType.VERSION, MOBILE_OS_VERSION);
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, DEVICE_NAME);
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
				capabilities.setCapability(CapabilityType.BROWSER_NAME, BROWSER_NAME);
				capabilities.setCapability("chromedriverExecutable", DRIVER_PATH);
				capabilities.setCapability("unicodekeyboard", true);

			//Test#2 - WebApp
			tc.seleniumDriver = new IOSDriver<WebElement>(new URL(APPIUM_URL), capabilities);
			tc.testWebCalculator();
		   
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	@AfterEach
	public void teardown() {
		//close the Mobile App
		appiumDriver.quit();
		
		//close the Mobile Browser
		seleniumDriver.quit();
	}
	
/*
	@AfterAll
	public void stopAppiumServer() throws IOException {
	    appiumService.stop();
	}
*/
}
