package org.springframework.samples.petclinic;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

@ExtendWith(ParasoftWatcher.class)
class NavigateTest {
	private static final String PETCLINIC_BASE_URL = "http://18.237.133.64:8099";
	private static final String GRID_URL = "http://18.237.133.64:4444/wd/hub";
	
	private static RemoteWebDriver driver;

//	private static WebDriver driver;
//	private static final String CHROME_DRIVER = "C:\\Users\\whaaker\\Downloads\\SOAVirt\\Extensions\\chromedriver_win64_(v119)\\chromedriver-win64\\chromedriver.exe";
	
	@BeforeAll
	static void openBrowser() {
		URL url = null;
		try {
			url = new URL(System.getProperty("GRID_URL", GRID_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ChromeOptions opts = new ChromeOptions();
		opts.addArguments("--remote-allow-origins=*");
		opts.addArguments("--headless");
		
		driver = new RemoteWebDriver(url, opts, false);
		
//		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);
//		driver = new ChromeDriver(opts);
	}
	
	@AfterAll
	static void closeBrowser() {
		if(driver != null) {
			driver.quit();
		}
	}

	@Test
	void testPetClinicNavigation() throws Exception {
		driver.get(System.getProperty("PETCLINIC_BASE_URL", PETCLINIC_BASE_URL) + "/");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@title=\"veterinarians\"]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@class=\"dropdown-toggle\"]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@ui-sref=\"owners\"]")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//owner-list/table/tbody/tr[1]/td[1]/a")).click();
		Thread.sleep(3000);
		driver.findElement(By.xpath("//dd/a")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@title=\"home page\"]")).click();
	}

}