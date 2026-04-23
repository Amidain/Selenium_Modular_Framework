package framework.core.configs;

import java.util.Objects;

import framework.core.driver.BrowserType;

/**
 * Typed runtime configuration for browser and execution mode settings.
 */
public class DriverConfiguration {

    private final BrowserType browser;
    private final String baseUrl;
    private final boolean headless;
    private final String gridUrl;
    private final boolean remoteRun;
    private final boolean dockerRun;
    private final boolean insertDbRecords;
    private final boolean autoScrollEnabled;

    DriverConfiguration(BrowserType browser,
                        String baseUrl,
                        boolean headless,
                        String gridUrl,
                        boolean remoteRun,
                        boolean dockerRun,
                        boolean insertDbRecords,
                        boolean autoScrollEnabled) {
        this.browser = Objects.requireNonNull(browser, "Configuration attribute <browser> is required and cannot be null.");
        this.baseUrl = Objects.requireNonNull(baseUrl, "Configuration attribute <baseUrl> is required and cannot be null.");
        this.headless = headless;
        this.gridUrl = Objects.requireNonNull(gridUrl, "Configuration attribute <gridUrl> is required and cannot be null.");
        this.remoteRun = remoteRun;
        this.dockerRun = dockerRun;
        this.insertDbRecords = insertDbRecords;
        this.autoScrollEnabled = autoScrollEnabled;
    }

    /**
     * @return the browser configured for the current run.
     */
    public BrowserType getBrowser() {
        return browser;
    }

    /**
     * @return the configured application base URL.
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @return true when browsers should run headless.
     */
    public boolean isHeadless() {
        return headless;
    }

    /**
     * @return the Selenium Grid URL used for remote execution.
     */
    public String getGridUrl() {
        return gridUrl;
    }

    /**
     * @return true when the run should use Selenium Grid or another remote endpoint.
     */
    public boolean isRemoteRun() {
        return remoteRun;
    }

    /**
     * @return true when the run targets a Docker-hosted browser setup.
     */
    public boolean isDockerRun() {
        return dockerRun;
    }

    /**
     * @return true when database records should be inserted during the run.
     */
    public boolean isInsertDbRecordsEnabled() {
        return insertDbRecords;
    }

    /**
     * @return true when UI auto-scrolling is enabled.
     */
    public boolean isAutoScrollEnabled() {
        return autoScrollEnabled;
    }
}