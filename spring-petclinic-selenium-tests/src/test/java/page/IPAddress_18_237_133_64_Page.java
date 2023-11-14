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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IPAddress_18_237_133_64_Page {

	@FindBy(name = "firstName")
	private WebElement firstNameField;

	@FindBy(name = "lastName")
	private WebElement lastNameField;

	@FindBy(name = "address")
	private WebElement addressField;

	@FindBy(name = "city")
	private WebElement cityField;

	@FindBy(name = "telephone")
	private WebElement telephoneField;

	@FindBy(xpath = "/descendant::button[normalize-space(.)='Submit']")
	private WebElement submitButton;

	@FindBy(linkText = "Mark Verdugo")
	private WebElement markVerdugoLink;

	@FindBy(linkText = "Add New Pet")
	private WebElement addNewPetLink;

	@FindBy(name = "name")
	private WebElement nameField;

	@FindBy(xpath = "//tr[1]/td[1]/a")
	private WebElement webElement;

	@FindBy(linkText = "Add Visit")
	private WebElement addVisitLink;

	@FindBy(xpath = "//label[text() = 'Description']/following-sibling::textarea")	
	private WebElement descriptionField;
	
	@FindBy(xpath = "//label[text() = 'Type']/following-sibling::div/select")
	private WebElement typeField;

	@FindBy(xpath = "/descendant::button[normalize-space(.)='Add New Visit']")
	private WebElement addNewVisitButton;

	@FindBy(linkText = "Wilhelm Haaker")
	private WebElement wilhelmHaakerLink;

	@FindBy(xpath = "//table/tbody/tr[1]/td[1]/dl/dd[1]/a")
	private WebElement field;

	@FindBy(xpath = "//label[text() = 'Birth date']/following-sibling::div/input")
	private WebElement birthDateField;
	
	@FindBy(xpath = "//label[text() = 'Date']/following-sibling::input")
	private WebElement visitDateField;
	
	private WebDriver driver;

	private static final Duration DEFAULT_WAIT_FOR_ELEMENT_TIMEOUT = Duration.ofSeconds(15);

	private static final String[] TITLE_WORDS = { "PetClinic", "::", "Spring", "Framework", "demonstration" };

	public IPAddress_18_237_133_64_Page(WebDriver driver) {
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

	public void clickWilhelmHaakerLink() {
		click(wilhelmHaakerLink);
	}

	public String getFieldText() {
		return waitFor(field).getText();
	}

	public void clickWebElement() {
		click(webElement);
	}

	public void clickAddVisitLink() {
		click(addVisitLink);
	}

	public void setDescriptionField(String text) {
		waitFor(descriptionField).clear();
		descriptionField.sendKeys(text);
	}

	public void clickAddNewVisitButton() {
		click(addNewVisitButton);
	}

	public void setFirstNameField(String text) {
		waitFor(firstNameField).clear();
		firstNameField.sendKeys(text);
	}

	public void setLastNameField(String text) {
		waitFor(lastNameField).clear();
		lastNameField.sendKeys(text);
	}

	public void setAddressField(String text) {
		waitFor(addressField).clear();
		addressField.sendKeys(text);
	}

	public void setCityField(String text) {
		waitFor(cityField).clear();
		cityField.sendKeys(text);
	}

	public void setTelephoneField(String text) {
		waitFor(telephoneField).clear();
		telephoneField.sendKeys(text);
	}

	public void clickSubmitButton() {
		click(submitButton);
	}

	public void clickMarkVerdugoLink() {
		click(markVerdugoLink);
	}

	public void clickAddNewPetLink() {
		click(addNewPetLink);
	}

	public void setNameField(String text) {
		waitFor(nameField).clear();
		nameField.sendKeys(text);
	}

	public void selectTypeDropdown(String text) {
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_FOR_ELEMENT_TIMEOUT);
		wait.ignoring(StaleElementReferenceException.class);
		wait.until(webdriver -> new Select(typeField).getOptions().stream()
				.anyMatch(element -> text.equals(element.getText())));
		Select dropdown = new Select(typeField);
		dropdown.selectByVisibleText(text);
	}

	public void setBirthDate(String text) {
		waitFor(birthDateField).clear();
		birthDateField.sendKeys(text);
	}

	public void setVisitDate(String text) {
		waitFor(visitDateField).clear();
		visitDateField.sendKeys(text);
	}

}