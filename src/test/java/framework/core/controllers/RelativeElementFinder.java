package framework.core.controllers;

import framework.elements.base.BaseElement;
import framework.elements.base.IBaseElement;
import framework.elements.factory.ElementFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation that scopes relative XPath locators to a parent element
 * and hydrates typed child component instances.
 */
public class RelativeElementFinder implements IRelativeElementFinder {

    private static final String XPATH_PREFIX = "By.xpath";

    @Override
    public <T extends BaseElement> List<T> findChildElements(IBaseElement parentElement,
                                                             String childElementName,
                                                             By relativeXPathLocator,
                                                             Class<T> typeOfElementClass,
                                                             ElementFactory elementFactory) {
        Objects.requireNonNull(parentElement, "<FindChildElements> parentElement not provided in method signature!");
        Objects.requireNonNull(relativeXPathLocator, "<FindChildElements> relativeXPathLocator not provided in method signature!");
        Objects.requireNonNull(typeOfElementClass, "<FindChildElements> typeOfElementClass not provided in method signature!");
        Objects.requireNonNull(elementFactory, "<FindChildElements> elementFactory not provided in method signature!");

        validateRelativeXPath(relativeXPathLocator);

        WebElement parentWebElement = parentElement.getWrappedElement();
        List<WebElement> rawChildren = parentWebElement.findElements(relativeXPathLocator);
        List<T> mappedChildren = new ArrayList<>(rawChildren.size());

        boolean appendIndex = rawChildren.size() > 1;
        for (int i = 0; i < rawChildren.size(); i++) {
            int index = i + 1;
            By indexedRelativeLocator = buildIndexedRelativeLocator(relativeXPathLocator, index);
            By childLocator = new ByChained(parentElement.getLocator(), indexedRelativeLocator);
            String formattedName = formatChildName(parentElement.getName(), childElementName, index, appendIndex);
            T child = elementFactory.create(typeOfElementClass, childLocator, formattedName);
            mappedChildren.add(child);
        }

        return mappedChildren;
    }

    private void validateRelativeXPath(By relativeLocator) {
        if (!(relativeLocator instanceof By.ByXPath)) {
            throw new IllegalArgumentException("Relative locator must be an XPath expression");
        }
        String expression = extractXPath(relativeLocator);
        if (!expression.startsWith(".")) {
            throw new IllegalArgumentException(String.format(
                    "Expected relative XPath starting with '.' but received: %s", expression));
        }
    }

    private By buildIndexedRelativeLocator(By relativeLocator, int index) {
        String expression = extractXPath(relativeLocator);
        String indexedExpression = String.format("(%s)[%d]", expression, index);
        return By.xpath(indexedExpression);
    }

    private String extractXPath(By locator) {
        String description = locator.toString();
        if (description.startsWith(XPATH_PREFIX)) {
            int colonIndex = description.indexOf(':');
            return description.substring(colonIndex + 1).trim();
        }
        return description;
    }

    private String formatChildName(String parentName, String childName, int index, boolean appendIndex) {
        String baseName = childName == null || childName.isBlank() ? "child" : childName;
        return appendIndex
                ? String.format("%s -> %s[%d]", parentName, baseName, index)
                : String.format("%s -> %s", parentName, baseName);
    }
}
