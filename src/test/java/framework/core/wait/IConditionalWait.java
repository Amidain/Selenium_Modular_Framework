package framework.core.wait;

import java.time.Duration;
import java.util.function.BooleanSupplier;

/**
 * Fluent waiting contract for element-centric state checks.
 *
 * <p>This interface is intended for use by framework element wrappers (e.g. BaseElement and its descendants)
 * to express wait logic in a readable, composable way without relying on Selenium {@code ExpectedConditions}.</p>
 *
 * <p>All {@code until*} methods block the current thread until the condition is satisfied or a timeout occurs.
 * On timeout, implementations typically throw {@link org.openqa.selenium.TimeoutException}.</p>
 */
public interface IConditionalWait {

    /**
     * Cosmetic helper to improve readability when chaining multiple waits.
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait and();

    /**
     * Overrides the timeout used by subsequent {@code until*} calls.
     *
     * @param timeout maximum time to wait; ignored when {@code null}, zero or negative
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait withTimeout(Duration timeout);

    /**
     * Sets the timeout to the configured "short" timeout (typically used for quick UI transitions).
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait withShortTimeout();

    /**
     * Overrides the polling interval used while waiting.
     *
     * @param polling polling interval; ignored when {@code null}, zero or negative
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait withPolling(Duration polling);

    /**
     * Sets the polling interval to the configured "short" polling interval.
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait withShortPolling();

    /**
     * Convenience method that applies both {@link #withShortTimeout()} and {@link #withShortPolling()}.
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait withShort();

    /**
     * Waits until an arbitrary condition becomes true.
     *
     * @param condition boolean condition to be evaluated repeatedly
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait until(BooleanSupplier condition);

    /**
     * Waits until an arbitrary condition becomes true.
     *
     * @param condition boolean condition to be evaluated repeatedly
     * @param failureMessage message used by the underlying wait implementation when timing out
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait until(BooleanSupplier condition, String failureMessage);

    /**
     * Non-throwing variant of {@link #until(BooleanSupplier, String)}.
     *
     * @param condition boolean condition to be evaluated repeatedly
     * @param failureMessage message used by the underlying wait implementation when timing out
     * @return {@code true} when the condition was satisfied, {@code false} on timeout
     */
    boolean waitFor(BooleanSupplier condition, String failureMessage);

    /**
     * Waits until the element exists in the DOM.
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilPresent();

    /**
     * Waits until the element no longer exists in the DOM.
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilNotPresent();

    /**
     * Waits until the element is displayed.
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilVisible();

    /**
     * Waits until the element is not displayed (or is absent).
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilNotVisible();

    /**
     * Waits until the element is displayed and enabled.
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilEnabled();

    /**
     * Waits until the element becomes disabled (or not interactable).
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilDisabled();

    /**
     * Waits until the element is considered clickable (displayed + enabled).
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilClickable();

    /**
     * Waits until the element is stable (its rectangle does not change between polling ticks).
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilStable();

    /**
     * Waits until the element's {@code value} attribute equals the expected value.
     *
     * @param expected expected value; {@code null} is treated as empty string
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilValueEquals(String expected);

    /**
     * Waits until the element's text contains the expected substring.
     *
     * @param expectedSubstring substring to be present; {@code null} is treated as empty string
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilTextContains(String expectedSubstring);

    /**
     * Waits until the given attribute on the element equals the expected value.
     *
     * @param attributeName name of the attribute to check
     * @param expected expected value; {@code null} is treated as empty string
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilAttributeEquals(String attributeName, String expected);

    /**
     * Waits until the element's text does not contain the given substring.
     *
     * @param unexpectedSubstring substring that must no longer be present; {@code null} is treated as empty string
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilTextDoesNotContain(String unexpectedSubstring);

    /**
     * Waits until the element's value does not contain the given substring.
     *
     * @param unexpectedValue substring that must no longer be present; {@code null} is treated as empty string
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilValueNotEquals(String unexpectedValue);

    /**
     * Waits until the page and element are considered ready for interaction.
     * Typical composition:
     *
     *     document ready state is "complete"
     *     no global blockers (e.g. overlays/spinners)
     *     element present + stable
     *
     * @return the same wait instance for fluent chaining
     */
    IConditionalWait untilUiReady();
}
