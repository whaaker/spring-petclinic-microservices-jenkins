package org.springframework.samples.petclinic;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
	
	private static final String PETCLINIC_BASE_URL = "http://54.201.169.117:8099";
	private static final String GRID_URL = "http://54.201.169.117:4444/wd/hub";
	private static final String CHROME_DRIVER = "C:\\Users\\whaaker\\Downloads\\SOAVirt\\Extensions\\chromedriver_win64_(v119)\\chromedriver-win64\\chromedriver.exe";
	//private static RemoteWebDriver driver;
	private static WebDriver driver;
	
	@BeforeAll
	static void openBrowser() {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);
		URL url = null;
		try {
			url = new URL(System.getProperty("GRID_URL", GRID_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		ChromeOptions opts = new ChromeOptions();
//		Map<String, Object> prefs = new HashMap<String, Object>();
//		prefs.put("profile.managed_default_content_settings.geolocation", 2);
//		prefs.put("profile.default_content_setting_values.notifications", 2);
//		opts.setExperimentalOption("prefs", prefs);
		opts.addArguments("--remote-allow-origins=*");
//		opts.addArguments("--start-maximized");
//		opts.addArguments("--incognito");
//		opts.addArguments("--enable-strict-powerful-feature-restrictions");
//		opts.addArguments("--ignore-ssl-errors=yes");
//		opts.addArguments("--ignore-certificate-errors");
		opts.addArguments("--headless");
//        opts.addArguments("--disable-gpu");
//        opts.addArguments("--no-sandbox");
		
		driver = new RemoteWebDriver(url, opts);
		
//		driver = new ChromeDriver(opts);
	}
	
	@AfterAll
	static void closeBrowser() {
		driver.close();
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
		Thread.sleep(1000);
		driver.findElement(By.xpath("//owner-list/table/tbody/tr[1]/td[1]/a")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//dd/a")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@title=\"home page\"]")).click();
	}

}