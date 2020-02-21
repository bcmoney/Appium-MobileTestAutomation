package com.oultoncollege;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * 
 * @author bcopeland
 */
@Tag("desktop-webapp")
public class SeleniumTest {
	private static WebDriver driver;
	private static Map<String, Object> vars;
	private static JavascriptExecutor js;

	@BeforeAll
	public static void setUp() {
//	    System.setProperty("webdriver.chrome.driver", "C:\\APPS\\TestAutomation\\Selenium\\WebDrivers\\chromedriver_win32\\chromedriver.exe");
//	    driver = new ChromeDriver();
		System.setProperty("webdriver.gecko.driver", "C:\\APPS\\TestAutomation\\Selenium\\WebDrivers\\FireFox\\geckodriver-v0.26.0-win64\\geckodriver.exe");
		driver = new FirefoxDriver();
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();
	}


	@Test
	public void canadaTaxCalculatorTest() {
		driver.get("https://www.canada.ca/en/revenue-agency/services/tax/businesses/topics/gst-hst-businesses/charge-collect-which-rate/calculator.html");
		driver.manage().window().setSize(new Dimension(1295, 695));
		driver.findElement(By.id("ProvOrTerr")).click();
		WebElement dropdown = driver.findElement(By.id("ProvOrTerr"));
		dropdown.findElement(By.xpath("//option[. = 'New Brunswick']")).click();
		driver.findElement(By.cssSelector("option:nth-child(5)")).click();
		driver.findElement(By.id("priceinput")).click();
		driver.findElement(By.id("priceinput")).sendKeys("100");
		driver.findElement(By.xpath("(//label[@id=\'SwitchLabel\'])[2]")).click();
		driver.findElement(By.name("BefOrAft")).click();
		driver.findElement(By.id("calculate")).click();
		assertEquals("$115.00", driver.findElement(By.id("Tot")).getText());
	}


	@AfterAll
	public static void tearDown() {
		driver.quit();
	}
	
}
