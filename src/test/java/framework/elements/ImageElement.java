package framework.elements;

import framework.elements.base.BaseElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.function.Supplier;

public class ImageElement extends BaseElement {

    public ImageElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }

    public String getSource() {
        return actionExecutor.performAction(getName(), "getSource", () -> {
            await().untilStable().and().untilVisible();
            return getWrappedElement().getAttribute("src");
        });
    }

    public String getAlt() {
        return actionExecutor.performAction(getName(), "getAlt", () -> {
            await().untilStable().and().untilVisible();
            return getWrappedElement().getAttribute("alt");
        });
    }

    public boolean isLoaded() {
        return actionExecutor.performAction(getName(), "isLoaded", () -> {
            await().untilStable().and().untilVisible();
            WebElement element = getWrappedElement();
            Object result = ((JavascriptExecutor) driver()).executeScript(
                    "return arguments[0].complete && arguments[0].naturalWidth > 0;", element);
            return Boolean.TRUE.equals(result);
        });
    }
}
