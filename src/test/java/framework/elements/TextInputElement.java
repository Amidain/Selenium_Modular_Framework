package framework.elements;

import java.time.Duration;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import framework.elements.base.BaseElement;

public class TextInputElement extends BaseElement {

    public TextInputElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }

    public void setValue(String value) {
        clearWithKeyboard();
        type(value);
    }

    public void appendValue(String value) {
        type(value);
    }


    public void setValueAndSubmit(String value) {
        setValue(value);
        type(Keys.ENTER);
    }

    public String getValue() {
        return actionExecutor.performAction(getName(), "getValue", () -> {
            await().untilUiReady().and().untilPresent();
            return getWrappedElement().getAttribute("value");
        });
    }

    public void type(CharSequence... keysToSend) {
        actionExecutor.performAction(getName(), "type", () -> {
            await().untilUiReady().and().untilStable().and().untilClickable();
            WebElement element = getWrappedElement();
            element.click();
            element.sendKeys(keysToSend);
            return null;
        });
    }

    public void typeJs(CharSequence... keysToSend) {
        actionExecutor.performAction(getName(), "type", () -> {
            await().untilUiReady().and().untilStable().and().untilClickable();
            js().setValue(keysToSend);
            return null;
        });
    }
    public void clear() {
        clear(Duration.ZERO);
    }

    public void clear(Duration pauseBeforeClear) {
        actionExecutor.performAction(getName(), "clear", () -> {
            await().untilUiReady().and().untilStable().and().untilVisible();

            if (pauseBeforeClear != null && !pauseBeforeClear.isZero() && !pauseBeforeClear.isNegative()) {
                try {
                    Thread.sleep(pauseBeforeClear.toMillis());
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }

            getWrappedElement().clear();
            return null;
        });
    }

    public void clearWithKeyboard() {
        actionExecutor.performAction(getName(), "clearWithKeyboard", () -> {
            await().untilUiReady().and().untilStable().and().untilClickable();
            WebElement element = getWrappedElement();
            element.click();
            element.sendKeys(Keys.CONTROL + "a");
            element.sendKeys(Keys.DELETE);
            return null;
        });
    }
}
