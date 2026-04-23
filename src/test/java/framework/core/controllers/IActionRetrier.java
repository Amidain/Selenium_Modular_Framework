package framework.core.controllers;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Retries an action or function when handledExceptions occurs.
 */
public interface IActionRetrier {

    /**
     * Retries the action when the handled exception occurs.
     * @param actionName Label for logging.
     * @param runnable Action to be applied.
     */
    void doWithRetry(String actionName, Runnable runnable);

    /**
     * Retries the function when the handled exception occurs.
     * @param actionName Label for logging.
     * @param function Function to be applied.
     * @param <T> Return type of function.
     * @return Result of the function.
     */
    <T> T doWithRetry(String actionName, Supplier<T> function);

    /**
     * Exceptions to be ignored during action retrying.
     * @return By the default implementation,
     * {@link StaleElementReferenceException}, {@link InvalidElementStateException}, {@link ElementClickInterceptedException}
     * are handled.
     */
    default List<Class<? extends Throwable>> getHandledExceptions() {
        return Arrays.asList(
                StaleElementReferenceException.class,
                InvalidElementStateException.class,
                ElementClickInterceptedException.class
        );
    }
}
