package pages.product;

import org.openqa.selenium.By;

import framework.elements.ButtonElement;
import framework.elements.TextFieldElement;
import framework.elements.TextInputElement;
import framework.elements.factory.ElementFactory;
import framework.elements.widgets.FormElement;
import pages.base.BasePage;

public class ProductDetailPage extends BasePage {
    
    private final ElementFactory elementFactory;
    //Locators
    private static final By PRODUCT_DETAILS_FORM = By.cssSelector(".product-details");
    private static final By QUANTITY_INPUT = By.id("quantity");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector(".btn-default.cart");
    private static final By PRODUCT_NAME = By.cssSelector(".product-information h2");

    //Elements
    private final FormElement productDetailsForm;
    private final TextInputElement quantityInput;
    private final ButtonElement addToCartButton;
    private final TextFieldElement productName;

    public ProductDetailPage(ElementFactory elementFactory) {
        super("Product Detail Page", PRODUCT_DETAILS_FORM, elementFactory);
        this.elementFactory = elementFactory;
        this.productDetailsForm = elementFactory.form(PRODUCT_DETAILS_FORM, "Product Details Form");
        this.quantityInput = elementFactory.textInput(QUANTITY_INPUT, "Quantity Input");
        this.addToCartButton = elementFactory.button(ADD_TO_CART_BUTTON, "Add to Cart Button");
        this.productName = elementFactory.textField(PRODUCT_NAME, "Product Name");
    }

    public ProductAddedToCartModalSection productAddedToCartModalSection() {
        return new ProductAddedToCartModalSection(elementFactory);
    }
    
    public boolean isProductDetailsDisplayed() {
        return productDetailsForm.exists() && productDetailsForm.isDisplayed();
    }

    public void setProductQuantity(String quantity) {
        quantityInput.scrollIntoView();
        quantityInput.setValue(quantity);
    }

    public void clickAddToCart() {
        addToCartButton.scrollIntoView();
        addToCartButton.click();
    }

    public String getProductName() {
        return productName.getText();
    }
}
