package framework.elements;

import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import framework.elements.base.BaseElement;

public class DropdownElement extends BaseElement {

    public DropdownElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }

    public void selectByVisibleText(String text) {
        actionExecutor.performAction(getName(), "selectByVisibleText", () -> {
            select().selectByVisibleText(text);
            return null;
        });
    }

    public void selectByValue(String value) {
        actionExecutor.performAction(getName(), "selectByValue", () -> {
            select().selectByValue(value);
            return null;
        });
    }

    public void selectByIndex(int index) {
        actionExecutor.performAction(getName(), "selectByIndex", () -> {
            select().selectByIndex(index);
            return null;
        });
    }

    public String getSelectedText() {
        return actionExecutor.performAction(getName(), "getSelectedText", () -> select().getFirstSelectedOption().getText());
    }

    public String getSelectedValue() {
        return actionExecutor.performAction(getName(), "getSelectedValue", () -> select().getFirstSelectedOption().getAttribute("value"));
    }

    private Select select() {
        await().untilUiReady().and().untilStable().and().untilClickable();
        return new Select(getWrappedElement());
    }
}
