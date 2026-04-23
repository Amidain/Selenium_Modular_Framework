package pages.homePage;

import org.openqa.selenium.By;

import framework.elements.ButtonElement;
import framework.elements.factory.ElementFactory;
import pages.base.BasePage;
import pages.product.ProductAddedToCartModalSection;

public class HomePage extends BasePage {

    private final ElementFactory elementFactory;

    //Locators
    private static final By SLIDER = By.id("slider");
    private static final By SELECT_PRODUCT_BUTTON = By.className("choose");

    //Elements
    private final ButtonElement selectProductButton;

    public HomePage(ElementFactory elementFactory) {
        super("Home Page", SLIDER, elementFactory);
        this.elementFactory = elementFactory;
        this.selectProductButton = elementFactory.button(SELECT_PRODUCT_BUTTON, "Select Product");
    }
    
    public ProductAddedToCartModalSection productAddedToCartModalSection() {
        return new ProductAddedToCartModalSection(elementFactory);
    }

    public void selectFirstProductToView() {
        selectProductButton.scrollIntoView();
        selectProductButton.click();
    }
}