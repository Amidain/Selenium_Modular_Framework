package pages.authorization;

import org.openqa.selenium.By;

import framework.core.controllers.ByTestId;
import framework.elements.ButtonElement;
import framework.elements.TextFieldElement;
import framework.elements.TextInputElement;
import framework.elements.factory.ElementFactory;
import pages.base.BasePage;

public class SignUpLoginPage extends BasePage {
    
    private ElementFactory elementFactory;
    
    //Locators
    private static final By LOGIN_FORM = By.className("login-form");
    private static final By EMAIL_INPUT_LOGIN = ByTestId.byTestId("login-email");
    private static final By PASSWORD_INPUT_LOGIN = ByTestId.byTestId("login-password");
    private static final By LOGIN_BUTTON = ByTestId.byTestId("login-button");
    private static final By SIGNUP_NAME_INPUT = ByTestId.byTestId("signup-name");
    private static final By SIGNUP_EMAIL_INPUT = ByTestId.byTestId("signup-email");
    private static final By SIGNUP_BUTTON = ByTestId.byTestId("signup-button");
    private static final By USER_SIGNUP_HEADER = By.cssSelector(".signup-form h2");

    //Elements
    private final TextInputElement loginInput;
    private final TextInputElement passwordInput;
    private final ButtonElement loginButton;
    private final TextInputElement signUpNameInput;
    private final TextInputElement signUpEmailInput;
    private final ButtonElement signUpButton;
    private final TextFieldElement userSignUpHeader;

    public SignUpLoginPage(ElementFactory elementFactory) {
        super("Sign Up/Login Page", LOGIN_FORM, elementFactory);
        this.elementFactory = elementFactory;

        this.loginInput = elementFactory.textInput(EMAIL_INPUT_LOGIN, "Login Input");
        this.passwordInput = elementFactory.textInput(PASSWORD_INPUT_LOGIN, "Password Input");
        this.loginButton = elementFactory.button(LOGIN_BUTTON, "Login Button");
        this.signUpNameInput = elementFactory.textInput(SIGNUP_NAME_INPUT, "Sign Up Name Input");
        this.signUpEmailInput = elementFactory.textInput(SIGNUP_EMAIL_INPUT, "Sign Up Email Input");
        this.signUpButton = elementFactory.button(SIGNUP_BUTTON, "Sign Up Button");
        this.userSignUpHeader = elementFactory.textField(USER_SIGNUP_HEADER, "User Sign Up Header");
        
    }

    public void login(String email, String password) {
        loginInput.type(email);
        passwordInput.type(password);
        loginButton.click();
    }

    public void signUp(String name, String email) {
        signUpNameInput.type(name);
        signUpEmailInput.type(email);
        signUpButton.click();
    }

    public String getUserSignUpHeaderText() {
        return userSignUpHeader.getText();
    }
}
