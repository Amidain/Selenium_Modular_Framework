package framework.elements.base;

import java.util.List;

import framework.elements.factory.ElementFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WrapsElement;

/**
 * Contract that describes the capabilities every framework element wrapper must expose.
 */
public interface IBaseElement extends WrapsElement {

    /**
     * @return locator used to resolve the underlying web element lazily
     */
    By getLocator();

    /**
     * @return name of the element, used for logging and reporting
     */
    String getName();

    /**
     * Clicks the element using the standard Selenium click API once it becomes clickable.
     */
    void click();

    /**
     * Performs a JavaScript-based click which can bypass overlay issues on stubborn elements.
     */
    void clickWithJs();

    /**
     * @return visible text value of the element after it becomes visible
     */
    String getText();

    /**
     * Reads a specific attribute from the element once present and visible.
     *
     * @param attributeName exact attribute key to fetch
     * @return attribute value or {@code null} when the attribute is absent
     */
    String getAttribute(String attributeName);

    /**
     * @return {@code true} if the wrapped element is displayed in the DOM
     */
    boolean isDisplayed();

    /**
     * @return {@code true} if the wrapped element is enabled and interactable
     */
    boolean isEnabled();

    /**
     * @return {@code true} when at least one matching element exists in the DOM
     */
    boolean exists();

    /**
     * Scrolls the element into view aligning it to the top of the viewport by default.
     */
    void scrollIntoView();

    /**
     * Scrolls the element into view with full control over the final alignment.
     *
     * @param alignToTop {@code true} to align the element with the top of the viewport; {@code false} to align with the bottom
     */
    void scrollIntoView(boolean alignToTop);

    /**
     * @return all child elements matching the provided relative XPath locator
     */
    <T extends BaseElement> List<T> findChildElements(String childElementName,
                                                      By relativeXPathLocator,
                                                      Class<T> typeOfElementClass,
                                                      ElementFactory elementFactory);
}
