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
import page.IPAddress_18_237_133_64_Page;
import page.PetClinicaSpringFrameworkdemonstrationPage;

@ExtendWith(ParasoftWatcher.class)
public class RegisterOwnerAndPetTest {

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
	 * Name: RegisterOwnerAndPet
	 * Recording file: RegisterOwnerAndPet.json
	 *
	 * Parasoft recorded Selenium test on Thu Nov 09 2023 10:44:18 GMT-0800 (Pacific Standard Time)
	 */
	@Test
	public void testRegisterOwnerAndPetTest() throws Throwable {
		driver.get(System.getProperty("BASE_URL", BASE_URL) + "/#!/welcome");

		PetClinicaSpringFrameworkdemonstrationPage petClinicaSpringFrameworkdemonstrationPage = new PetClinicaSpringFrameworkdemonstrationPage(
				driver);
		Thread.sleep(1000);
		petClinicaSpringFrameworkdemonstrationPage.clickHOMELink();
		petClinicaSpringFrameworkdemonstrationPage.clickOWNERSLink();
		petClinicaSpringFrameworkdemonstrationPage.clickREGISTERLink();
		Thread.sleep(1000);
		
		IPAddress_18_237_133_64_Page iPAddress_18_237_133_64_Page = new IPAddress_18_237_133_64_Page(driver);
		Thread.sleep(1000);
		iPAddress_18_237_133_64_Page.setFirstNameField("Mark");
		iPAddress_18_237_133_64_Page.setLastNameField("Verdugo");
		iPAddress_18_237_133_64_Page.setAddressField("101 E. Huntington Dr.");
		iPAddress_18_237_133_64_Page.setCityField("Monrovia");
		iPAddress_18_237_133_64_Page.setTelephoneField("6267391734");
		iPAddress_18_237_133_64_Page.clickSubmitButton();
		Thread.sleep(1000);
		iPAddress_18_237_133_64_Page.clickMarkVerdugoLink();
		iPAddress_18_237_133_64_Page.clickAddNewPetLink();
		iPAddress_18_237_133_64_Page.setNameField("Arty");
		iPAddress_18_237_133_64_Page.setBirthDate("02022010");
		iPAddress_18_237_133_64_Page.selectTypeDropdown("dog");
		iPAddress_18_237_133_64_Page.clickSubmitButton();
		Thread.sleep(1000);
	}

}