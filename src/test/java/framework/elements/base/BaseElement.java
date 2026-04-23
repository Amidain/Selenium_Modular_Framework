package framework.elements.base;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import framework.core.controllers.ActionExecutor;
import framework.core.controllers.IActionExecutor;
import framework.core.controllers.IRelativeElementFinder;
import framework.core.controllers.RelativeElementFinder;
import framework.core.js.JsActions;
import framework.core.wait.ConditionalWait;
import framework.core.wait.IConditionalWait;
import framework.elements.factory.ElementFactory;

/**
 * Base wrapper around a raw WebElement that centralises driver access, logging, and waiting.
 */
public class BaseElement implements IBaseElement {

    private final By locator;
    private final String name;
    protected final IActionExecutor actionExecutor;
    private final IRelativeElementFinder relativeElementFinder;
    protected final Supplier<WebDriver> driverProvider;

    protected BaseElement(By locator, String name, Supplier<WebDriver> driverProvider) {
        this.locator = Objects.requireNonNull(locator, "Element locator must not be null!");
        this.name = name == null || name.isBlank() ? locator.toString() : name;
        this.actionExecutor = new ActionExecutor();
        this.relativeElementFinder = new RelativeElementFinder();
        this.driverProvider = driverProvider;
    }

    protected WebDriver driver() {
        return driverProvider.get();
    }

    public IConditionalWait await() {
        return new ConditionalWait(driverProvider, locator, name);
    }

    public JsActions js() {
        return new JsActions(driverProvider, locator);
    }

    @Override
    public WebElement getWrappedElement() {
        return driver().findElement(locator);
    }

    @Override
    public By getLocator() {
        return locator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void click() {
        actionExecutor.performAction(getName(), "click", () -> {
            await().untilUiReady().and().untilStable().and().untilClickable();
            getWrappedElement().click();
            return null;
        });
    }

    @Override
    public void clickWithJs() {
        actionExecutor.performAction(getName(), "clickWithJs", () -> {
            await().untilUiReady().and().untilStable().and().untilVisible();
            js().click();
            return null;
        });
    }

    @Override
    public String getText() {
        return actionExecutor.performAction(getName(), "getText", () -> {
            await().untilUiReady().and().untilStable().and().untilVisible();
            return getWrappedElement().getText();
        });
    }

    @Override
    public String getAttribute(String attributeName) {
        return actionExecutor.performAction(getName(), "getAttribute", () -> {
            await().untilUiReady().and().untilPresent();
            return getWrappedElement().getAttribute(attributeName);
        });
    }

    @Override
    public boolean isDisplayed() {
        return actionExecutor.performAction(getName(), "isDisplayed", () -> getWrappedElement().isDisplayed());
    }

    @Override
    public boolean isEnabled() {
        return actionExecutor.performAction(getName(), "isEnabled", () -> getWrappedElement().isEnabled());
    }

    @Override
    public boolean exists() {
        return actionExecutor.performAction(getName(), "exists", () -> !driver().findElements(locator).isEmpty());
    }

    @Override
    public void scrollIntoView() {
        scrollIntoView(true);
    }

    @Override
    public void scrollIntoView(boolean alignToCenter) {
        actionExecutor.performAction(getName(), "scrollIntoView", () -> {
            await().untilUiReady().and().untilStable().and().untilVisible();
            String block = alignToCenter ? "center" : "nearest";
            js().scroll(block);
            return null;
        });
    }

    @Override
    public <T extends BaseElement> List<T> findChildElements(String childElementName,
                                                             By relativeXPathLocator,
                                                             Class<T> typeOfElementClass,
                                                             ElementFactory elementFactory) {
        return actionExecutor.performAction(getName(), String.format("Locate child elements: <%s>", childElementName), () -> {
            await().untilUiReady().and().untilStable().and().untilPresent();
            return relativeElementFinder.findChildElements(this, childElementName, relativeXPathLocator, typeOfElementClass, elementFactory);
        });
    }
}
