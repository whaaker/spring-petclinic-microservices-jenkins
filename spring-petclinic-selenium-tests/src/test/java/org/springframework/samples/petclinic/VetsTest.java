/**
 * 
 */
package org.springframework.samples.petclinic;


import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import page.PetClinicaSpringFrameworkdemonstrationPage;

@ExtendWith(ParasoftWatcher.class)
public class VetsTest {

	/**
	 * Parasoft auto generated base URL
	 * Use -DBASE_URL=http://localhost:8080 from command line
	 * or use System.setProperty("BASE_URL", "http://localhost:8080") to change base URL at run time.
	 */
	private static final String BASE_URL = "http://18.237.133.64:8099";

	private WebDriver driver;

	@BeforeEach
	public void beforeTest() {
		ChromeOptions opts = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.managed_default_content_settings.geolocation", 2);
		prefs.put("profile.default_content_setting_values.notifications", 2);
		opts.setExperimentalOption("prefs", prefs);
		opts.addArguments("--start-maximized");
		opts.addArguments("--incognito");
		opts.addArguments("--enable-strict-powerful-feature-restrictions");
		driver = new ChromeDriver(opts);
		driver.manage().window().maximize();
	}

	@AfterEach
	public void afterTest() {
		if (driver != null) {
			driver.quit();
		}
	}

	/**
	 * Name: VetsTest
	 * Recording file: VetsTest.json
	 *
	 * Parasoft recorded Selenium test on Thu Nov 09 2023 09:56:38 GMT-0800 (Pacific Standard Time)
	 */
	@Test
	public void testVetsTest() throws Throwable {
		driver.get(System.getProperty("BASE_URL", BASE_URL) + "/#!/welcome");

		PetClinicaSpringFrameworkdemonstrationPage petClinicaSpringFrameworkdemonstrationPage = new PetClinicaSpringFrameworkdemonstrationPage(
				driver);
		Thread.sleep(2000);
		petClinicaSpringFrameworkdemonstrationPage.clickWebElement();
		Thread.sleep(1000);
		petClinicaSpringFrameworkdemonstrationPage.clickWebElement2();
	}

}