package framework.core.wait;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class AppStateWaits {
    private final Supplier<WebDriver> driverProvider;
    private final GlobalBlockerElements blockers;

    public AppStateWaits(Supplier<WebDriver> driverProvider) {
        this.driverProvider = Objects.requireNonNull(driverProvider, "driverProvider");
        this.blockers = new GlobalBlockerElements();
    }

    public BooleanSupplier waitForDocumentReady() {
        return () -> {
            try {
                WebDriver driver = driverProvider.get();
                if (!(driver instanceof JavascriptExecutor js)) {
                    // If the driver does not support JS, don't block readiness forever.
                    return true;
                }
                Object state = js.executeScript("return document.readyState");
                return "complete".equals(String.valueOf(state));
            } catch (WebDriverException e) {
                return false;
            }
        };
    }

    public BooleanSupplier waitUntilNoGlobalBlockers() {
        return () -> {
            try {
                WebDriver d = driverProvider.get();
                return blockers.getGlobalBlockerLocators().stream()
                        .allMatch(locator ->
                                d.findElements(locator).stream().noneMatch(e -> {
                                    try {
                                        return e.isDisplayed();
                                    } catch (StaleElementReferenceException ex) {
                                        return false;
                                    }
                                })
                        );
            } catch (WebDriverException e) {
                return false;
            }
        };
    }
}
