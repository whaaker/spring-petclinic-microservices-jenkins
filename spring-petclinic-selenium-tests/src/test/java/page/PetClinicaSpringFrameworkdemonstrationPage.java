/**
 * 
 */
package page;

import static org.openqa.selenium.support.ui.ExpectedConditions.attributeContains;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

import java.time.Duration;
import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PetClinicaSpringFrameworkdemonstrationPage {

	@FindBy(linkText = "HOME")
	private WebElement hOMELink;

	@FindBy(linkText = "OWNERS")
	private WebElement oWNERSLink;

	@FindBy(linkText = "REGISTER")
	private WebElement rEGISTERLink;

	@FindBy(xpath = "//div[2]/ul/li[2]/a")
	private WebElement webElement22;

	@FindBy(xpath = "//li[2]/ul/li[1]/a")
	private WebElement webElement3;

	@FindBy(xpath = "/descendant::button[normalize-space(.)='Submit']")
	private WebElement submitButton;

	@FindBy(xpath = "//div[2]/ul/li[1]/a")
	private WebElement webElement;

	@FindBy(xpath = "//li[3]/a")
	private WebElement webElement2;

	private WebDriver driver;

	private static final Duration DEFAULT_WAIT_FOR_ELEMENT_TIMEOUT = Duration.ofSeconds(15);

	private static final String[] TITLE_WORDS = { "PetClinic", "::", "Spring", "Framework", "demonstration" };

	public PetClinicaSpringFrameworkdemonstrationPage(WebDriver driver) {
		this.driver = driver;
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_FOR_ELEMENT_TIMEOUT);
		wait.ignoring(StaleElementReferenceException.class);
		Arrays.stream(TITLE_WORDS).forEach(word -> {
			wait.until(attributeContains(By.tagName("title"), "innerHTML", word));
		});
		PageFactory.initElements(driver, this);
	}

	private WebElement waitFor(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_FOR_ELEMENT_TIMEOUT);
		wait.ignoring(StaleElementReferenceException.class);
		return wait.until(elementToBeClickable(element));
	}

	private WebElement scrollTo(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", element);
		return element;
	}

	protected WebElement click(WebElement element) {
		WebElement webElement = scrollTo(waitFor(element));
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_FOR_ELEMENT_TIMEOUT);
		return wait.ignoring(ElementClickInterceptedException.class).until(webDriver -> {
			webElement.click();
			return webElement;
		});
	}

	public void clickWebElement() {
		click(webElement);
	}

	public void clickWebElement2() {
		click(webElement2);
	}

	public void clickSubmitButton() {
		click(submitButton);
	}

	public void clickWebElement22() {
		click(webElement22);
	}

	public void clickWebElement3() {
		click(webElement3);
	}

	public void clickHOMELink() {
		click(hOMELink);
	}

	public void clickOWNERSLink() {
		click(oWNERSLink);
	}

	public void clickREGISTERLink() {
		click(rEGISTERLink);
	}

}