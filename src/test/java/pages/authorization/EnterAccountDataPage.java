package pages.authorization;

import org.openqa.selenium.By;

import framework.core.controllers.ByTestId;
import framework.elements.ButtonElement;
import framework.elements.CheckboxElement;
import framework.elements.DropdownElement;
import framework.elements.TextFieldElement;
import framework.elements.TextInputElement;
import framework.elements.factory.ElementFactory;
import pages.base.BasePage;

public class EnterAccountDataPage extends BasePage {
    
    private ElementFactory elementFactory;
    //Locators
    private static final By UNIQUE_ELEMENT = By.cssSelector(".login-form");
    private static final By ENTER_ACCOUNT_INFORMATION_HEADER = By.cssSelector(".login-form h2");
    private static final By MR_RADIO_BUTTON = By.id("id_gender1");
    private static final By MRS_RADIO_BUTTON = By.id("id_gender2");
    private static final By NAME_INPUT = By.id("name");
    private static final By EMAIL_INPUT = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By DAY_DROPDOWN = By.id("days");
    private static final By MONTH_DROPDOWN = By.id("months");
    private static final By YEAR_DROPDOWN = By.id("years");
    private static final By NEWSLETTER_CHECKBOX = By.id("newsletter");
    private static final By OFFERS_CHECKBOX = By.id("optin");
    private static final By FIRST_NAME_INPUT = By.id("first_name");
    private static final By LAST_NAME_INPUT = By.id("last_name");
    private static final By COMPANY_INPUT = By.id("company");
    private static final By ADDRESS_INPUT = By.id("address1");
    private static final By ADDRESS2_INPUT = By.id("address2");
    private static final By COUNTRY_DROPDOWN = By.id("country");
    private static final By STATE_INPUT = By.id("state");
    private static final By CITY_INPUT = By.id("city");
    private static final By ZIPCODE_INPUT = By.id("zipcode");
    private static final By MOBILE_NUMBER_INPUT = By.id("mobile_number");
    private static final By CREATE_ACCOUNT_BUTTON = ByTestId.byTestId("create-account");
    
    //Elements
    private TextFieldElement enterAccountInformationHeader;
    private ButtonElement mrRadioButton;
    private ButtonElement mrsRadioButton;
    private TextInputElement nameInput;
    private TextInputElement emailInput;
    private TextInputElement passwordInput;
    private DropdownElement dayDropdown; 
    private DropdownElement monthDropdown;
    private DropdownElement yearDropdown;
    private CheckboxElement newsletterCheckbox;
    private CheckboxElement offersCheckbox;
    private TextInputElement firstNameInput;
    private TextInputElement lastNameInput;
    private TextInputElement companyInput;
    private TextInputElement addressInput;
    private TextInputElement address2Input;
    private DropdownElement countryDropdown;
    private TextInputElement stateInput;
    private TextInputElement cityInput;
    private TextInputElement zipcodeInput;
    private TextInputElement mobileNumberInput;
    private ButtonElement createAccountButton;


    public EnterAccountDataPage(ElementFactory elementFactory) {
        super("Create Account Section", UNIQUE_ELEMENT, elementFactory);
        this.elementFactory = elementFactory;
        this.enterAccountInformationHeader = elementFactory.textField(ENTER_ACCOUNT_INFORMATION_HEADER, "Enter Account Information Header");
        this.mrRadioButton = elementFactory.button(MR_RADIO_BUTTON, "Mr. Button");
        this.mrsRadioButton = elementFactory.button(MRS_RADIO_BUTTON, "Mrs. Button");
        this.nameInput = elementFactory.textInput(NAME_INPUT, "Name Input");
        this.emailInput = elementFactory.textInput(EMAIL_INPUT, "Email Input");
        this.passwordInput = elementFactory.textInput(PASSWORD_INPUT, "Password Input");
        this.dayDropdown = elementFactory.dropdown(DAY_DROPDOWN, "Day Dropdown");
        this.monthDropdown = elementFactory.dropdown(MONTH_DROPDOWN, "Month Dropdown");
        this.yearDropdown = elementFactory.dropdown(YEAR_DROPDOWN, "Year Dropdown");
        this.newsletterCheckbox = elementFactory.checkbox(NEWSLETTER_CHECKBOX, "Newsletter Checkbox");
        this.offersCheckbox = elementFactory.checkbox(OFFERS_CHECKBOX, "Offers Checkbox");
        this.firstNameInput = elementFactory.textInput(FIRST_NAME_INPUT, "First Name Input");
        this.lastNameInput = elementFactory.textInput(LAST_NAME_INPUT, "Last Name Input");
        this.companyInput = elementFactory.textInput(COMPANY_INPUT, "Company Input");
        this.addressInput = elementFactory.textInput(ADDRESS_INPUT, "Address Input");
        this.address2Input = elementFactory.textInput(ADDRESS2_INPUT, "Address2 Input");
        this.countryDropdown = elementFactory.dropdown(COUNTRY_DROPDOWN, "Country Dropdown");
        this.stateInput = elementFactory.textInput(STATE_INPUT, "State Input");
        this.cityInput = elementFactory.textInput(CITY_INPUT, "City Input");
        this.zipcodeInput = elementFactory.textInput(ZIPCODE_INPUT, "Zipcode Input");
        this.mobileNumberInput = elementFactory.textInput(MOBILE_NUMBER_INPUT, "Mobile Number Input");
        this.createAccountButton = elementFactory.button(CREATE_ACCOUNT_BUTTON, "Create Account Button");
    }

    public void selectTitle(String title) {
        if (title.equalsIgnoreCase("Mr.")) {
            mrRadioButton.click();
        } else if (title.equalsIgnoreCase("Mrs.")) {
            mrsRadioButton.click();
        }
    }

    public void fillAccountInformation (String password, String day, String month, String year) {
        passwordInput.scrollIntoView();
        passwordInput.setValue(password);
        dayDropdown.scrollIntoView();
        dayDropdown.selectByValue(day);
        monthDropdown.selectByValue(month);
        yearDropdown.selectByValue(year);
    }

    public String getName() {
        return nameInput.getValue();
    }

    public String getEmail() {
        return emailInput.getValue();
    }

    public void selectNewsletterSubscription() {
        newsletterCheckbox.scrollIntoView();
        newsletterCheckbox.check();
    }

    public void selectOffersSubscription() {
        offersCheckbox.scrollIntoView();
        offersCheckbox.check();
    }

    public void fillAddressInformation(String firstName, String lastName, String company, String address, String address2, String country, String state, String city, String zipcode, String mobileNumber) {
        firstNameInput.scrollIntoView();
        firstNameInput.setValue(firstName);
        lastNameInput.scrollIntoView();
        lastNameInput.setValue(lastName);
        companyInput.scrollIntoView();
        companyInput.setValue(company);
        addressInput.setValue(address);
        address2Input.setValue(address2);
        countryDropdown.scrollIntoView();
        countryDropdown.selectByValue(country);
        stateInput.setValue(state);
        cityInput.setValue(city);
        zipcodeInput.scrollIntoView();
        zipcodeInput.setValue(zipcode);
        mobileNumberInput.scrollIntoView();
        mobileNumberInput.setValue(mobileNumber);
    }

    public void clickCreateAccount() {
        createAccountButton.scrollIntoView();
        createAccountButton.click();
    }

    public String getEnterAccountInformationHeaderText() {
        return enterAccountInformationHeader.getText();
    }
}
