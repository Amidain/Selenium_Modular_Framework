package pages.cart;

import org.openqa.selenium.By;

import framework.elements.TextFieldElement;
import framework.elements.factory.ElementFactory;
import pages.base.BasePage;

public class CartPage extends BasePage {
    private final ElementFactory elementFactory;

    //Locators
    private static final By CART_PAGE = By.id("cart_items");
    private static final By PRODUCT_QUANTITY_TEXT = By.cssSelector(".cart_quantity button");
    private static final By PRODUCT_NAME = By.cssSelector(".cart_description h4 a");
    //Elements
    private TextFieldElement productQuantityText;
    private TextFieldElement productNameText;

    public CartPage(ElementFactory elementFactory) {
        super("Cart Page", CART_PAGE, elementFactory);
        this.elementFactory = elementFactory;
        this.productQuantityText = elementFactory.textField(PRODUCT_QUANTITY_TEXT, "Product Quantity Text");
        this.productNameText = elementFactory.textField(PRODUCT_NAME, "Product Name Text");

    }

    public String getProductQuantity() {
        return productQuantityText.getText();
    }

    public String getProductName() {
        return productNameText.getText();
    }
}
