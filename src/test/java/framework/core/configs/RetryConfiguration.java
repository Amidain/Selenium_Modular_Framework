package framework.core.configs;

import java.time.Duration;
import java.util.Objects;

public class RetryConfiguration {

    private final int retryCount;
    private final Duration pollingInterval;

    RetryConfiguration(int retryCount, Duration pollingInterval) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("Configuration attribute <retryCount> must not be negative.");
        }
        this.retryCount = retryCount;
        this.pollingInterval = Objects.requireNonNull(pollingInterval, "Configuration attribute <pollingInterval> is required and cannot be null.");
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Duration getPollingInterval() {
        return pollingInterval;
    }
}
