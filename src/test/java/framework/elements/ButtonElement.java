package framework.elements;

import framework.elements.base.BaseElement;
import org.openqa.selenium.*;

import java.util.function.Supplier;

/**
 * Convenience wrapper for clickable buttons.
 */
public class ButtonElement extends BaseElement {

    public ButtonElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }

    public String getLabel() {
        return actionExecutor.performAction(getName(), "getLabel", () -> {
            await().untilStable().and().untilVisible();
            WebElement element = getWrappedElement();
            String text = element.getText();
            return text != null && !text.isBlank() ? text : element.getAttribute("value");
        });
    }

    public void clickAndWaitToDisappear() {
        actionExecutor.performAction(getName(), "clickAndWaitToDisappear", () -> {
            await().untilClickable();
            getWrappedElement().click();
            await().untilNotVisible();
            return null;
        });
    }
}
