package framework.elements.widgets;

import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import framework.elements.base.BaseElement;

/**
 * Represents a cohesive form section (container + child elements).
 */
public class FormElement extends BaseElement {

    public FormElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        super(locator, name, driverProvider);
    }
}
