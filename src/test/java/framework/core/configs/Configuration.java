package framework.core.configs;

import java.time.Duration;
import java.util.Locale;

import framework.core.driver.BrowserType;

/**
 * Central access point for framework configuration.
 *
 * Configuration is immutable at runtime and can only be supplied when the process starts.
 */
public final class Configuration {
    private final RetryConfiguration retryConfiguration;
    private final WaitConfiguration waitConfiguration;
    private final DriverConfiguration driverConfiguration;
    private final ReportingConfiguration reportingConfiguration;
    private final LocatorConfiguration locatorConfiguration;

    private Configuration() {
        this.retryConfiguration = new RetryConfiguration(
            readInt("retry.count"),
            readMillisDuration("retry.delay.millis")
        );
        this.waitConfiguration = new WaitConfiguration(
            readSecondsDuration("default.timeout.seconds"),
            readSecondsDuration("short.timeout.seconds"),
            readMillisDuration("default.polling.millis"),
            readMillisDuration("short.polling.millis")
        );
        this.driverConfiguration = new DriverConfiguration(
            readBrowserType("browser"),
            readString("base.url"),
            readBoolean("headless"),
            readString("grid.url"),
            readBoolean("is.remote.run"),
            readBoolean("docker"),
            readBoolean("insert.db.records"),
            readBoolean("ui.autoscroll.enabled")
        );
        this.reportingConfiguration = new ReportingConfiguration(
            readBoolean("reporting.screenshots.on.failure"),
            readBoolean("reporting.verbose.ui.logging")
        );
        this.locatorConfiguration = new LocatorConfiguration(
            readString("test.id.attribute")
        );
    }

    public static RetryConfiguration retryConfiguration() {
        return getInstance().retryConfiguration;
    }

    public static WaitConfiguration waitConfiguration() {
        return getInstance().waitConfiguration;
    }

    public static DriverConfiguration driverConfiguration() {
        return getInstance().driverConfiguration;
    }

    public static ReportingConfiguration reporting() {
        return getInstance().reportingConfiguration;
    }

    public static LocatorConfiguration locatorConfiguration() {
        return getInstance().locatorConfiguration;
    }

    static Configuration getInstance() {
        return Holder.INSTANCE;
    }

    private String readString(String key) {
        String rawValue = resolveRawValue(key);
        return rawValue;
    }

    private int readInt(String key) {
        String rawValue = resolveRawValue(key);
        try {
            return Integer.parseInt(rawValue);
        } catch (RuntimeException error) {
            throw new IllegalStateException("Failed to parse integer configuration for key '" + key + "': " + rawValue, error);
        }
    }

    private boolean readBoolean(String key) {
        String rawValue = resolveRawValue(key);
        return Boolean.parseBoolean(rawValue);
    }

    private Duration readSecondsDuration(String key) {
        String rawValue = resolveRawValue(key);
        try {
            return Duration.ofSeconds(Long.parseLong(rawValue));
        } catch (RuntimeException error) {
            throw new IllegalStateException("Failed to parse seconds duration configuration for key '" + key + "': " + rawValue, error);
        }
    }

    private Duration readMillisDuration(String key) {
        String rawValue = resolveRawValue(key);
        try {
            return Duration.ofMillis(Long.parseLong(rawValue));
        } catch (RuntimeException error) {
            throw new IllegalStateException("Failed to parse milliseconds duration configuration for key '" + key + "': " + rawValue, error);
        }
    }

    private BrowserType readBrowserType(String key) {
        String rawValue = resolveRawValue(key);
        try {
            return BrowserType.valueOf(rawValue.toUpperCase(Locale.ROOT));
        } catch (RuntimeException error) {
            throw new IllegalStateException("Failed to parse browser configuration for key '" + key + "': " + rawValue, error);
        }
    }

    private String resolveRawValue(String key) {
        return PropertyReader.getParam(key);    
    }

    private static final class Holder {
        private static final Configuration INSTANCE = new Configuration();
    }
}