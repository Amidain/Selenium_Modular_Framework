package framework.elements;

import framework.elements.base.BaseElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

public class LinkElement extends BaseElement {

    public LinkElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }

    public String getHref() {
        return actionExecutor.performAction(getName(), "getHref", () -> {
            await().untilStable().and().untilVisible();
            return getWrappedElement().getAttribute("href");
        });
    }

    public String getTitle() {
        return actionExecutor.performAction(getName(), "getTitle", () -> {
            await().untilStable().and().untilVisible();
            return getWrappedElement().getAttribute("title");
        });
    }
}
