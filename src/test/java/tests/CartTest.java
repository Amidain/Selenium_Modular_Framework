package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import pages.cart.CartPage;
import pages.homePage.HomePage;
import pages.product.ProductDetailPage;
import tests.base.BaseTest;
import utils.DriverUtils;

public class CartTest extends BaseTest {

    @Test(description = "Test Case 13: Verify Product quantity in Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void whenAddProductToCartThenQuantityIsCorrect() {
        HomePage homePage = new HomePage(elementFactory);
        CartPage cartPage = new CartPage(elementFactory);
        ProductDetailPage productDetailPage = new ProductDetailPage(elementFactory);

        DriverUtils.navigateToPage("https://automationexercise.com/");
        homePage.waitForPage();
        Assert.assertTrue(homePage.isPageDisplayed(), "Home page is not displayed");
        homePage.selectFirstProductToView();
        productDetailPage.waitForPage();
        Assert.assertTrue(productDetailPage.isPageDisplayed(), "Product detail page is not displayed");
        final String productName = productDetailPage.getProductName();
        productDetailPage.setProductQuantity("4");
        productDetailPage.clickAddToCart();
        productDetailPage.productAddedToCartModalSection().waitForPage();
        productDetailPage.productAddedToCartModalSection().clickViewCartButton();
        cartPage.waitForPage();
        Assert.assertTrue(cartPage.isPageDisplayed(), "Cart page is not displayed");
        Assert.assertEquals(cartPage.getProductQuantity(), "4", "Product quantity in cart is not correct");
        Assert.assertEquals(cartPage.getProductName(), productName, "Product name in cart is not correct");
    }
}
