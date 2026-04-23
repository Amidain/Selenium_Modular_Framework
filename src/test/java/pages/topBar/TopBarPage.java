package pages.topBar;

import org.openqa.selenium.By;

import framework.elements.ButtonElement;
import framework.elements.TextFieldElement;
import framework.elements.factory.ElementFactory;
import pages.base.BasePage;

public class TopBarPage extends BasePage {
    private ElementFactory elementFactory;
    //Locators
    private static final By UNIQUE_ELEMENT = By.id("header");
    private static final By HOME_BUTTON = By.className("fa-home");
    private static final By PRODUCTS_BUTTON = By.className("card_travel");
    private static final By CART_BUTTON = By.className("fa-shopping-cart");
    private static final By LOGIN_BUTTON = By.className("fa-lock");
    private static final By CONTACT_US_BUTTON = By.className("fa-envelope");
    private static final By LOGGED_IN_AS_TEXT = By.cssSelector("ul a b");
    private static final By DELETE_ACCOUNT_BUTTON = By.className("fa-trash-o");

    //Elements
    private final ButtonElement homeButton;
    private final ButtonElement productsButton; 
    private final ButtonElement cartButton;
    private final ButtonElement loginButton;
    private final ButtonElement contactUsButton;
    private final TextFieldElement loggedInAsText;
    private final ButtonElement deleteAccountButton;

    public TopBarPage(ElementFactory elementFactory) {
        super("Top Bar Page", UNIQUE_ELEMENT, elementFactory);
        this.elementFactory = elementFactory;
        this.homeButton = elementFactory.button( HOME_BUTTON, "Home button");
        this.productsButton = elementFactory.button( PRODUCTS_BUTTON, "Products button");
        this.cartButton = elementFactory.button( CART_BUTTON, "Cart button");
        this.loginButton = elementFactory.button( LOGIN_BUTTON, "Login button");
        this.contactUsButton = elementFactory.button( CONTACT_US_BUTTON, "Contact Us button");
        this.loggedInAsText = elementFactory.textField( LOGGED_IN_AS_TEXT, "Logged In As Text");
        this.deleteAccountButton = elementFactory.button( DELETE_ACCOUNT_BUTTON, "Delete Account button");
    }

    public void clickHome() {
        homeButton.click();
    }

    public void clickProducts() {
        productsButton.click();
    }

    public void clickCart() {
        cartButton.click();
    }

    public void clickLogin() {
        loginButton.click();
    }

    public void clickContactUs() {
        contactUsButton.click();
    }

    public String getLoggedInAsText() {
        return loggedInAsText.getText();
    }

    public void clickDeleteAccount() {
        deleteAccountButton.click();
    }
}
