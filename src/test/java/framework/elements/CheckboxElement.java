package framework.elements;

import framework.elements.base.BaseElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

public class CheckboxElement extends BaseElement {

    public CheckboxElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }

    public void check() {
        actionExecutor.performAction(getName(), "check", () -> {
            if (!isChecked()) {
                click();
            }
            return null;
        });
    }

    public void uncheck() {
        actionExecutor.performAction(getName(), "uncheck", () -> {
            if (isChecked()) {
                click();
            }
            return null;
        });
    }

    public boolean isChecked() {
        return actionExecutor.performAction(getName(),  "isChecked", () -> {
            await().untilStable().and().untilVisible();
            return getWrappedElement().isSelected();
        });
    }

}
