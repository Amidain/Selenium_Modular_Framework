package pages.product;

import org.openqa.selenium.By;

import framework.elements.ButtonElement;
import framework.elements.factory.ElementFactory;
import pages.base.BasePage;

public class ProductAddedToCartModalSection extends BasePage {
    
    private final ElementFactory elementFactory;
    
    //Locators
    private static final By MODAL = By.id("cartModal");
    private static final By CONTINUE_SHOPPING_BUTTON = By.cssSelector(".btn.btn-success.close-modal.btn-block");
    private static final By VIEW_CART_BUTTON = By.cssSelector(".modal-body u");

    //Elements
    private final ButtonElement continueShoppingButton;
    private final ButtonElement viewCartButton;

    public ProductAddedToCartModalSection(ElementFactory elementFactory) {
        super("Product Added To Cart Modal Section", MODAL, elementFactory);
        this.elementFactory = elementFactory;
        this.continueShoppingButton = elementFactory.button(CONTINUE_SHOPPING_BUTTON, "Continue Shopping Button");
        this.viewCartButton = elementFactory.button(VIEW_CART_BUTTON, "View Cart Button");
    }

    public void clickContinueShoppingButton() {
        continueShoppingButton.scrollIntoView();
        continueShoppingButton.click();
    }

    public void clickViewCartButton() {
        viewCartButton.scrollIntoView();
        viewCartButton.click();
    }
}
