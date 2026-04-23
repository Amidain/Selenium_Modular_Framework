package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import pages.authorization.AccountMessagePage;
import pages.authorization.EnterAccountDataPage;
import pages.authorization.SignUpLoginPage;
import pages.homePage.HomePage;
import pages.topBar.TopBarPage;
import testData.AccountData;
import testData.AddressData;
import tests.base.BaseTest;
import utils.DriverUtils;

public class RegisterTest extends BaseTest {

    //Constants
    private final AccountData USER = new AccountData();
    private final AddressData ADDRESS = new AddressData();
    private final String SIGNUP_HEADER_TEXT = "New User Signup!";
    private final String CREATE_ACCOUNT_HEADER_TEXT = "ENTER ACCOUNT INFORMATION";

    @Test(description = "Test Case 1: Register User")
    @Severity(SeverityLevel.CRITICAL)
    public void whenProvideValidRegistrationDetailsThenUserIsRegistered() {
        HomePage homePage = new HomePage(elementFactory);
        TopBarPage topBarPage = new TopBarPage(elementFactory);
        SignUpLoginPage signUpLoginPage = new SignUpLoginPage(elementFactory);
        AccountMessagePage accountMessagePage = new AccountMessagePage(elementFactory);
        EnterAccountDataPage enterAccountDataPage = new EnterAccountDataPage(elementFactory);

        DriverUtils.navigateToPage("https://automationexercise.com/");
        homePage.waitForPage();
        Assert.assertTrue(homePage.isPageDisplayed(), "Home page is not displayed");
        topBarPage.clickLogin();
        signUpLoginPage.waitForPage();
        Assert.assertTrue(signUpLoginPage.isPageDisplayed(), "Sign Up/Login page is not displayed");
        Assert.assertEquals(signUpLoginPage.getUserSignUpHeaderText(), SIGNUP_HEADER_TEXT, "Sign Up header text is not correct");
        signUpLoginPage.signUp(USER.getName(), USER.getEmail());
        enterAccountDataPage.waitForPage();
        Assert.assertEquals(enterAccountDataPage.getEnterAccountInformationHeaderText(), CREATE_ACCOUNT_HEADER_TEXT, "Create Account form is not displayed");
        enterAccountDataPage.selectTitle(USER.getTitle());
        Assert.assertEquals(enterAccountDataPage.getName(), USER.getName(), "Name is not correct");
        Assert.assertEquals(enterAccountDataPage.getEmail(), USER.getEmail(), "Email is not correct");
        enterAccountDataPage.fillAccountInformation(USER.getPassword(), USER.getDay(), USER.getMonth(), USER.getYear());
        enterAccountDataPage.selectNewsletterSubscription();
        enterAccountDataPage.selectOffersSubscription();
        enterAccountDataPage.fillAddressInformation(ADDRESS.getFirstName(), ADDRESS.getLastName(), ADDRESS.getCompany(), ADDRESS.getAddress(), ADDRESS.getAddress2(), ADDRESS.getCountry(), ADDRESS.getState(), ADDRESS.getCity(), ADDRESS.getZipcode(), ADDRESS.getMobileNumber());
        enterAccountDataPage.clickCreateAccount();
        accountMessagePage.waitForPage();
        Assert.assertTrue(accountMessagePage.isAccountCreatedMessageDisplayed(), "Account created message is not displayed");
        accountMessagePage.clickContinueButton();
        Assert.assertEquals(topBarPage.getLoggedInAsText(), USER.getName());
        topBarPage.clickDeleteAccount();
        accountMessagePage.waitForPage();
        Assert.assertTrue(accountMessagePage.isAccountDeletedMessageDisplayed(), "Account deleted message is not displayed");
        accountMessagePage.clickContinueButton();
    }
}
