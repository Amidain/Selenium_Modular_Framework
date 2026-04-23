package framework.elements;

import framework.elements.base.BaseElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

public class TextFieldElement extends BaseElement {

    public TextFieldElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }

    public String getAriaLabel() {
        return actionExecutor.performAction(getName(), "getAriaLabel", () -> {
            await().untilStable().and().untilVisible();
            return getWrappedElement().getAttribute("aria-label");
        });
    }
}
