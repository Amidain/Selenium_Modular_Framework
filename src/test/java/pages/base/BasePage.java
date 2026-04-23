package pages.base;

import org.openqa.selenium.By;

import framework.elements.factory.ElementFactory;
import framework.elements.widgets.FormElement;

public abstract class BasePage {
    private final String pageName;
    private final By uniqueElementLocator;
    private final ElementFactory elementFactory;
    
    public BasePage(String pageName, By uniqueElementLocator, ElementFactory elementFactory) {
        this.pageName = pageName;
        this.uniqueElementLocator = uniqueElementLocator;
        this.elementFactory = elementFactory;
    }

    public By getUniqueElementLocator() {
        return uniqueElementLocator;
    }

    public ElementFactory getElementFactory() {
        return elementFactory;
    }

    public String getPageName() {
        return pageName;
    }

    /**
     * Waits until the unique selector becomes visible, signalling that the page is ready for interaction.
     */
    public void waitForPage() {
        FormElement uniqueElement = getElementFactory().form(getUniqueElementLocator(), String.format("Unique element of page: <%s>", getPageName()));
        uniqueElement.await().untilUiReady().and().untilVisible();
    }

    public boolean isPageDisplayed() {
        FormElement uniqueElement = getElementFactory().form(getUniqueElementLocator(), String.format("Unique element of page: <%s>", getPageName()));
        return uniqueElement.exists();
    }
}
