package framework.core.js;

import java.util.List;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class JsActions {
    private final Supplier<WebDriver> driverProvider;
    private final By locator;

    public JsActions(Supplier<WebDriver> driver, By locator){
        this.driverProvider = driver;
        this.locator = locator;
    }

    public JsActions blur(){
        ((JavascriptExecutor) driverProvider.get())
                .executeScript("arguments[0].blur();", findFirst());
        return this;
    }

    public JsActions focus(WebElement element){
        ((JavascriptExecutor) driverProvider.get())
                .executeScript("arguments[0].focus({preventScroll:true});", element);
        return this;
    }

    public String getRawValue() {
        return (String) ((JavascriptExecutor) driverProvider.get())
                .executeScript("return arguments[0].value;", findFirst());
    }

    public JsActions click() {
        ((JavascriptExecutor) driverProvider.get()).executeScript("arguments[0].click();", findFirst());
        return this;
    }

    public JsActions scroll(String block) {
        ((JavascriptExecutor) driverProvider.get()).executeScript(
                "arguments[0].scrollIntoView({block: arguments[1], inline: 'nearest'});",
                findFirst(),
                block
        );
        return this;
    }

    public JsActions clear() {
        ((JavascriptExecutor) driverProvider.get()).executeScript("arguments[0].value = '';" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                findFirst()
        );
        return this;
    }

    public JsActions setValue(CharSequence ...chars) {
        String value = String.join("", chars);
        ((JavascriptExecutor) driverProvider.get()).executeScript("arguments[0].value = arguments[1];", findFirst(), value);
        ((JavascriptExecutor) driverProvider.get()).executeScript(
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                findFirst()
        );
        return this;
    }

    private WebElement findFirst() {
        try {
            List<WebElement> elements = driverProvider.get().findElements(locator);
            return (elements == null || elements.isEmpty()) ? null : elements.getFirst();
        } catch (WebDriverException e) {
            return null;
        }
    }
}
