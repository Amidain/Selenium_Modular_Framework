package framework.core.driver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;

import framework.core.configs.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Builds thread-safe WebDriver instances with isolated driver services and artifact directories.
 * Each factory instance is immutable, making it safe to use inside ThreadLocal DriverManager providers.
 */
public class BrowserFactory {

    private static final String MESSAGE_UNKNOWN_BROWSER = "Unknown browser type! Please check your configuration";
    private static final Path DRIVER_ARTIFACT_ROOT = initDriverArtifactRoot();

    private final BrowserType browserType;
    private final boolean headless;
    private final boolean isRemoteRun;

    public BrowserFactory(BrowserType browserType, boolean isRemoteRun, boolean headless) {
        this.browserType = browserType;
        this.isRemoteRun = isRemoteRun;
        this.headless = headless;
    }

    public WebDriver getBrowser() {
        return isRemoteRun ? createRemoteDriver() : createLocalDriver();
    }

    private WebDriver createLocalDriver() {
        switch (browserType) {
            case CHROME -> {
                return createLocalChromeDriver();
            }
            default -> throw new IllegalStateException(MESSAGE_UNKNOWN_BROWSER);
        }
    }

    private WebDriver createRemoteDriver() {
        switch (browserType) {
            case CHROME -> {
                return new RemoteWebDriver(buildGridUrl(), buildChromeOptions(null));
            }
            default -> throw new IllegalStateException(MESSAGE_UNKNOWN_BROWSER);
        }
    }

    /**
     * Spins up a local ChromeDriver with per-thread logging and profile directories.
     */
    private WebDriver createLocalChromeDriver() {
        Path artifactsDir = createThreadArtifactDir("chrome");
        ChromeOptions chromeOptions = buildChromeOptions(artifactsDir);

        ChromeDriverService service = ServiceFactory.createChromeService(artifactsDir);

        WebDriverManager.chromedriver().setup();

        try {
            return new ChromeDriver(service, chromeOptions);
        } catch (Exception e) {
            System.err.println("Failed to create ChromeDriver: " + e.getMessage());
            throw new RuntimeException("Failed to create ChromeDriver", e);
        }
    }

    /**
     * Builds ChromeOptions shared by local and remote runs and wires logging/profile dirs when available.
     */
    private ChromeOptions buildChromeOptions(Path artifactsDir) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--remote-allow-origins=*",
                "--window-size=1920,1080",
                "--disable-background-timer-throttling",
                "--disable-backgrounding-occluded-windows",
                "--disable-renderer-backgrounding",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--lang=pl",
                "--disable-notifications",
                "--disable-infobars",
                "--disable-extensions",
                "--disable-popup-blocking");

        if (headless) {
            chromeOptions.addArguments("--headless=new");
        }

        if (artifactsDir != null) {
            Path profileDir = artifactsDir.resolve("profile");
            Path cacheDir = artifactsDir.resolve("cache");
            ensureDirectory(profileDir);
            ensureDirectory(cacheDir);
            chromeOptions.addArguments("--user-data-dir=" + profileDir.toAbsolutePath());
            chromeOptions.addArguments("--disk-cache-dir=" + cacheDir.toAbsolutePath());
        }

        LoggingPreferences chromeLogs = buildBrowserLoggingPreferences();
        chromeOptions.setCapability("goog:loggingPrefs", chromeLogs);
        return chromeOptions;
    }

    /**
     * Returns logging preferences that capture both browser console and driver logs.
     */
    private LoggingPreferences buildBrowserLoggingPreferences() {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        logPrefs.enable(LogType.DRIVER, Level.ALL);
        return logPrefs;
    }

    
      /**
       *  Resolves the Selenium Grid URL while surfacing configuration issues early.
       */
    private URL buildGridUrl() {
        try {
            return URI.create(Configuration.driverConfiguration().getGridUrl()).toURL();
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new IllegalStateException("Failed to create RemoteWebDriver due to malformed grid URL", e);
        }
    }


    /**
     * Creates the root artifact directory once per JVM to cache browser-specific logs.
     */
    private static Path initDriverArtifactRoot() {
        Path path = Paths.get("target", "driver-artifacts");
        try {
            Files.createDirectories(path);
            return path;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize driver artifacts directory", e);
        }
    }

    /**
     * Ensures every thread/session combo receives its own artifact folder (logs, profiles, cache).
     */
    private Path createThreadArtifactDir(String browserKey) {
        Path browserRoot = DRIVER_ARTIFACT_ROOT.resolve(browserKey);
        Path threadRoot = browserRoot.resolve("thread-" + Thread.currentThread().threadId());
        Path sessionRoot = threadRoot.resolve("session-" + UUID.randomUUID());
        try {
            Files.createDirectories(sessionRoot);
            return sessionRoot;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create driver artifact directory for " + browserKey, e);
        }
    }

    private void ensureDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create directory " + path, e);
        }
    }
}
