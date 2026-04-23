package framework.core.configs;

/**
 * Typed runtime configuration for framework reporting and diagnostics.
 */
public class ReportingConfiguration {

    private final boolean screenshotOnFailureEnabled;
    private final boolean verboseUiLoggingEnabled;

    ReportingConfiguration(boolean screenshotOnFailureEnabled, boolean verboseUiLoggingEnabled) {
        this.screenshotOnFailureEnabled = screenshotOnFailureEnabled;
        this.verboseUiLoggingEnabled = verboseUiLoggingEnabled;
    }

    /**
     * @return true when screenshots should be captured on failures.
     */
    public boolean isScreenshotOnFailureEnabled() {
        return screenshotOnFailureEnabled;
    }

    /**
     * @return true when verbose UI interaction logging is enabled.
     */
    public boolean isVerboseUiLoggingEnabled() {
        return verboseUiLoggingEnabled;
    }
}