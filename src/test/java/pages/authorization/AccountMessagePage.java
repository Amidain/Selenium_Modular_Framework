package pages.authorization;

import org.openqa.selenium.By;

import framework.core.controllers.ByTestId;
import framework.elements.ButtonElement;
import framework.elements.TextFieldElement;
import framework.elements.factory.ElementFactory;
import pages.base.BasePage;

public class AccountMessagePage extends BasePage {
 
    private ElementFactory elementFactory;
    //Locators
    private static final By UNIQUE_ELEMENT = By.className("col-sm-9");
    private static final By ACCOUNT_CREATED_MESSAGE = ByTestId.byTestId("account-created");
    private static final By CONTINUE_BUTTON = ByTestId.byTestId("continue-button");
    private static final By ACCOUNT_DELETED_MESSAGE = ByTestId.byTestId("account-deleted");

    //Elements
    private TextFieldElement accountCreatedMessage;
    private ButtonElement continueButton;
    private TextFieldElement accountDeletedMessage;

    public AccountMessagePage(ElementFactory elementFactory) {
        super("Account Message Section", UNIQUE_ELEMENT, elementFactory);
        this.elementFactory = elementFactory;
        this.accountCreatedMessage = elementFactory.textField(ACCOUNT_CREATED_MESSAGE, "Account Created Header");
        this.continueButton = elementFactory.button(CONTINUE_BUTTON, "Continue Button");
        this.accountDeletedMessage = elementFactory.textField(ACCOUNT_DELETED_MESSAGE, "Account Deleted Message");
    }

    public String getAccountCreatedHeaderText() {
        return accountCreatedMessage.getText();
    }

    public boolean isAccountCreatedMessageDisplayed() {
        return accountCreatedMessage.exists();
    }

    public void clickContinueButton() {
        continueButton.click();
    }
    
    public String getAccountDeletedMessageText() {
        return accountDeletedMessage.getText();
    }

    public boolean isAccountDeletedMessageDisplayed() {
        return accountDeletedMessage.exists();
    }
}