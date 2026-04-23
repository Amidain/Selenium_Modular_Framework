package framework.core.wait;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import framework.core.configs.Configuration;

public final class ConditionalWait implements IConditionalWait {

    private static final Duration FALLBACK_DEFAULT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration FALLBACK_DEFAULT_POLLING = Duration.ofMillis(500);

    private final Supplier<WebDriver> driverProvider;
    private final By locator;
    private final String elementName;
    private final AppStateWaits appStateWaits;
    private Duration timeout;
    private Duration polling;

    public ConditionalWait(Supplier<WebDriver> driverProvider, By locator, String elementName) {
        this.driverProvider = Objects.requireNonNull(driverProvider, "driverProvider");
        this.locator = locator;
        this.elementName = (elementName == null || elementName.isBlank()) ? "<element>" : elementName;
        this.appStateWaits = new AppStateWaits(this.driverProvider);
        this.timeout = resolveDefaultTimeout();
        this.polling = resolveDefaultPolling();
    }

    @Override
    public ConditionalWait and() {
        return this;
    }

    @Override
    public ConditionalWait withTimeout(Duration timeout) {
        if (timeout != null && !timeout.isZero() && !timeout.isNegative()) {
            this.timeout = timeout;
        }
        return this;
    }

    @Override
    public ConditionalWait withShortTimeout() {
        this.timeout = resolveShortTimeout();
        return this;
    }

    @Override
    public ConditionalWait withPolling(Duration polling) {
        if (polling != null && !polling.isZero() && !polling.isNegative()) {
            this.polling = polling;
        }
        return this;
    }

    @Override
    public ConditionalWait withShortPolling() {
        this.polling = resolveShortPolling();
        return this;
    }

    @Override
    public ConditionalWait withShort() {
        return withShortTimeout().withShortPolling();
    }

    @Override
    public ConditionalWait until(BooleanSupplier condition) {
        return until(condition, String.format("Condition was not satisfied within timeout for %s", elementName));
    }

    @Override
    public ConditionalWait until(BooleanSupplier condition, String failureMessage) {
        Objects.requireNonNull(condition, "condition");

        WebDriverWait wait = new WebDriverWait(driverProvider.get(), timeout);
        wait.pollingEvery(polling);
        wait.ignoring(StaleElementReferenceException.class);
        wait.ignoring(NoSuchElementException.class);
        if (failureMessage != null && !failureMessage.isBlank()) {
            wait.withMessage(failureMessage);
        }

        wait.until(driver -> condition.getAsBoolean());
        return this;
    }

    @Override
    public boolean waitFor(BooleanSupplier condition, String failureMessage) {
        try {
            until(condition, failureMessage);
            return true;
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    @Override   
    public ConditionalWait untilPresent() {
        return until(this::isPresent, String.format("Element '%s' was not present", elementName));
    }


    @Override   
    public ConditionalWait untilNotPresent() {
        return until(() -> !isPresent(), String.format("Element '%s' was still present", elementName));
    }


    @Override   
    public ConditionalWait untilVisible() {
        return until(this::isVisible, String.format("Element '%s' was not visible", elementName));
    }


    @Override   
    public ConditionalWait untilNotVisible() {

        return until(() -> !isVisible(), String.format("Element '%s' was still visible", elementName));
    }


    @Override   
    public ConditionalWait untilEnabled() {
        return until(this::isEnabled, String.format("Element '%s' was not enabled", elementName));
    }


    @Override   
    public ConditionalWait untilDisabled() {
        return until(() -> !isEnabled(), String.format("Element '%s' was still enabled", elementName));
    }

    @Override   
    public ConditionalWait untilClickable() {
        return until(() -> {
            WebElement element = findFirst();
            if (element == null) {
                return false;
            }
            try {
                return element.isDisplayed() && element.isEnabled();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        }, String.format("Element '%s' was not clickable", elementName));
    }

    

    @Override   
    public ConditionalWait untilStable() {
        AtomicReference<Rectangle> previous = new AtomicReference<>();
        AtomicReference<Instant> previousInstant = new AtomicReference<>();

        return until(() -> {
            WebElement element = findFirst();
            if (element == null) {
                return false;
            }
            try {
                if (!element.isDisplayed()) {
                    return false;
                }
                Rectangle current = element.getRect();
                Rectangle before = previous.getAndSet(current);
                Instant now = Instant.now();
                Instant beforeInstant = previousInstant.getAndSet(now);
                if (before == null || beforeInstant == null) {
                    return false;
                }
                // Require at least some time between snapshots (protects against drivers returning cached rects instantly).
                if (Duration.between(beforeInstant, now).compareTo(Duration.ofMillis(100)) < 0) {
                    return false;
                }
                return current.equals(before);
            } catch (WebDriverException e) {
                return false;
            }
        }, String.format("Element '%s' was not stable", elementName));
    }


    @Override   
    public ConditionalWait untilValueEquals(String expected) {
        String expectedNonNull = expected == null ? "" : expected;
        return until(() -> {
            WebElement element = findFirst();
            if (element == null) {
                return false;
            }
            try {
                String value = element.getAttribute("value");
                return Objects.equals(expectedNonNull, value == null ? "" : value);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        }, String.format("Element '%s' value did not match expected", elementName));
    }

    @Override
    public ConditionalWait untilValueNotEquals(String unexpectedValue) {
        String unexpected = unexpectedValue == null ? "" : unexpectedValue;
        return until(() -> {
            WebElement element = findFirst();
            if (element == null) {
                return false;
            }
            try {
                String value = element.getAttribute("value");
                return !Objects.equals(unexpected, value == null ? "" : value);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        }, String.format("Element '%s' still had unexpected value: '%s'!", elementName, unexpectedValue));
    }

    @Override   
    public ConditionalWait untilTextContains(String expectedSubstring) {
        String expected = expectedSubstring == null ? "" : expectedSubstring;
        return until(() -> {
            WebElement element = findFirst();
            if (element == null) {
                return false;
            }
            try {
                String text = element.getText();
                return text != null && text.contains(expected);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        }, String.format("Element '%s' text did not contain expected substring", elementName));
    }

    @Override
    public ConditionalWait untilTextDoesNotContain(String unexpectedSubstring) {
        String unexpected = unexpectedSubstring == null ? "" : unexpectedSubstring;
        return until(() -> {
            WebElement element = findFirst();
            if (element == null) {
                return false;
            }
            try {
                String text = element.getText();
                return text == null || !text.contains(unexpected);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        }, String.format("Element '%s' still had unexpected text!", elementName));
    }

    @Override
    public ConditionalWait untilUiReady() {
        until(() -> appStateWaits.waitForDocumentReady().getAsBoolean(), "Waiting for document to be in state 'complete' failed!");
        until(() -> appStateWaits.waitUntilNoGlobalBlockers().getAsBoolean(), "Waiting for no presence of global blockers failed!");
        return this;
    }

    @Override
    public ConditionalWait untilAttributeEquals(String attributeName, String expected) {
        String expectedNonNull = expected == null ? "" : expected;
        return until(() -> {
            WebElement element = findFirst();
            if (element == null) {
                return false;
            }
            try {
                String value = element.getAttribute(attributeName);
                return Objects.equals(expectedNonNull, value == null ? "" : value);
            } catch (StaleElementReferenceException e) {
                return false;
            }
        }, String.format("Elements '%s' attribute '%s' did not match expected value: '%s'!", elementName, attributeName, expected));
    }

    private boolean isPresent() {
        try {
            List<WebElement> elements = driverProvider.get().findElements(locator);
            return elements != null && !elements.isEmpty();
        } catch (WebDriverException e) {
            return false;
        }
    }

    private boolean isVisible() {
        WebElement element = findFirst();
        if (element == null) {
            return false;
        }
        try {
            return element.isDisplayed();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    private boolean isEnabled() {
        WebElement element = findFirst();
        if (element == null) {
            return false;
        }
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    private WebElement findFirst() {
        try {
            List<WebElement> elements = driverProvider.get().findElements(locator);
            return (elements == null || elements.isEmpty()) ? null : elements.getFirst();
        } catch (WebDriverException e) {
            return null;
        }
    }

    private static Duration resolveDefaultTimeout() {
        try {
            Duration configured = Configuration.waitConfiguration().getDefaultTimeoutSeconds();
            return (configured == null || configured.isZero() || configured.isNegative())
                    ? FALLBACK_DEFAULT_TIMEOUT
                    : configured;
        } catch (RuntimeException ignored) {
            return FALLBACK_DEFAULT_TIMEOUT;
        }
    }

    private static Duration resolveDefaultPolling() {
        try {
            Duration configured = Configuration.waitConfiguration().getDefaultPollingMillis();
            return (configured == null || configured.isZero() || configured.isNegative())
                    ? FALLBACK_DEFAULT_POLLING
                    : configured;
        } catch (RuntimeException ignored) {
            return FALLBACK_DEFAULT_POLLING;
        }
    }

    private static Duration resolveShortTimeout() {
        try {
            Duration configured = Configuration.waitConfiguration().getShortTimeoutSeconds();
            return (configured == null || configured.isZero() || configured.isNegative())
                    ? FALLBACK_DEFAULT_TIMEOUT
                    : configured;
        } catch (RuntimeException ignored) {
            return FALLBACK_DEFAULT_TIMEOUT;
        }
    }

    private static Duration resolveShortPolling() {
        try {
            Duration configured = Configuration.waitConfiguration().getShortPollingMillis();
            return (configured == null || configured.isZero() || configured.isNegative())
                    ? FALLBACK_DEFAULT_POLLING
                    : configured;
        } catch (RuntimeException ignored) {
            return FALLBACK_DEFAULT_POLLING;
        }
    }
}
