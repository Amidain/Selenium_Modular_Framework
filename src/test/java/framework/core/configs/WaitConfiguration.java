package framework.core.configs;

import java.time.Duration;
import java.util.Objects;

public class WaitConfiguration {

    private final Duration defaultTimeout;
    private final Duration shortTimeout;
    private final Duration defaultPolling;
    private final Duration shortPolling;

    WaitConfiguration(Duration defaultTimeout,
                      Duration shortTimeout,
                      Duration defaultPolling,
                      Duration shortPolling) {
        this.defaultTimeout = Objects.requireNonNull(defaultTimeout, "Configuration attribute <defaultTimeout> is required and cannot be null.");
        this.shortTimeout = Objects.requireNonNull(shortTimeout, "Configuration attribute <shortTimeout> is required and cannot be null.");
        this.defaultPolling = Objects.requireNonNull(defaultPolling, "Configuration attribute <defaultPolling> is required and cannot be null.");
        this.shortPolling = Objects.requireNonNull(shortPolling, "Configuration attribute <shortPolling> is required and cannot be null.");
    }

    public Duration getDefaultTimeoutSeconds() {
        return defaultTimeout;
    }

    public Duration getShortTimeoutSeconds() {
        return shortTimeout;
    }

    public Duration getDefaultPollingMillis() {
        return defaultPolling;
    }

    public Duration getShortPollingMillis() {
        return shortPolling;
    }
}
