package framework.core.driver;
import java.util.Objects;
import java.util.function.Supplier;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import framework.core.configs.Configuration;

/**
 * Centralized access point for thread-safe WebDriver instances.
 * Drivers are lazily instantiated per-thread using a pluggable supplier, and
 * disposed alongside the associated ThreadLocal metadata to support parallel runs.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> WEB_DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserType> BROWSER_TYPE = new ThreadLocal<>();
    private static Supplier<WebDriver> driverProvider = defaultDriverProvider();

    /**
     * Allow tests or runners to override how drivers are created (e.g. custom capabilities, cloud providers).
     */
    public static void setDriverProvider(Supplier<WebDriver> provider) {
        driverProvider = Objects.requireNonNull(provider, "driverProvider");
    }

    /**
     * Returns the current thread's WebDriver, lazily creating it when first requested.
     */
    public static WebDriver getWebDriver() {
        WebDriver webDriver = WEB_DRIVER.get();
        if (webDriver == null || isSessionClosed(webDriver)) {
            webDriver = driverProvider.get();
            WEB_DRIVER.set(webDriver);
        }
        return webDriver;
    }

    /**
     * Exposes the browser type associated with the current thread, defaulting to configuration.
     */
    public static BrowserType getBrowserType() {
        BrowserType type = BROWSER_TYPE.get();
        if (type == null) {
            type = Configuration.driverConfiguration().getBrowser();
            BROWSER_TYPE.set(type);
        }
        return type;
    }

    /**
     * Quits and removes the current thread's driver, ensuring no leakage across parallel tests.
     */
    public static void disposeDriver() {
        WebDriver webDriver = WEB_DRIVER.get();
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (Exception e) {
                System.err.println("Failed to quit WebDriver cleanly: " + e.getMessage());
            } finally {
                WEB_DRIVER.remove();
                BROWSER_TYPE.remove();
            }
        }
    }

    /**
     * Default provider that builds drivers using the BrowserFactory + TestRunProperties settings.
     */
    private static Supplier<WebDriver> defaultDriverProvider() {
        return () -> {
            BrowserType type = Configuration.driverConfiguration().getBrowser();
            BROWSER_TYPE.set(type);
            return new BrowserFactory(
                type, 
                Configuration.driverConfiguration().isRemoteRun(), 
                Configuration.driverConfiguration().isHeadless()
                ).getBrowser();
        };
    }

    /**
     * Basic heuristic to detect whether a cached session has been dropped by the grid/driver.
     */
    private static boolean isSessionClosed(WebDriver driver) {
        try {
            driver.getTitle();  
            return false;
        } catch (WebDriverException e) {
            String message = e.getMessage();
            return message != null && (message.contains("Invalid session id")
                || message.contains("Session not found"));
        }
    }
}
