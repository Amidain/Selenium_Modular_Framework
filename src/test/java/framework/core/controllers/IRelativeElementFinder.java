package framework.core.controllers;

import framework.elements.base.BaseElement;
import framework.elements.base.IBaseElement;
import framework.elements.factory.ElementFactory;
import org.openqa.selenium.By;

import java.util.List;

/**
 * Resolves child component instances located via relative XPath expressions scoped to a specific parent element.
 */
public interface IRelativeElementFinder {

    /**
     * Locates every descendant that matches the provided relative XPath locator and maps each result to the requested
     * {@link BaseElement} subtype using the supplied {@link ElementFactory}.
     *
     * @param parentElement        concrete widget/element acting as the root search context
     * @param childElementName     friendly label assigned to the child elements for logging and reporting
     * @param relativeXPathLocator relative XPath (must start with '.') evaluated within the parent element context
     * @param typeOfElementClass   concrete {@link BaseElement} subtype to instantiate for each match
     * @param elementFactory       factory responsible for constructing typed child elements
     * @return ordered list of instantiated child elements (possibly empty when no matches)
     */
    <T extends BaseElement> List<T> findChildElements(IBaseElement parentElement,
                                                      String childElementName,
                                                      By relativeXPathLocator,
                                                      Class<T> typeOfElementClass,
                                                      ElementFactory elementFactory);
}
