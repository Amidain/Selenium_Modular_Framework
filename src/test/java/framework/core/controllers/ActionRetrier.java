package framework.core.controllers;

import java.util.Collection;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import framework.core.configs.RetryConfiguration;

public class ActionRetrier implements IActionRetrier {

    private static final Logger LOGGER = LogManager.getLogger(ActionRetrier.class);
    private static final String DEFAULT_ACTION_NAME = "unnamed-action";

    private final RetryConfiguration retryConfiguration;
    private final Collection<Class<? extends Throwable>> handledExceptions;

    public ActionRetrier(RetryConfiguration retryConfiguration) {
        this.retryConfiguration = retryConfiguration;
        this.handledExceptions = IActionRetrier.super.getHandledExceptions();
    }

    @Override
    public void doWithRetry(String actionName, Runnable runnable) {
        Supplier<?> supplier = () -> {
            runnable.run();
            return true;
        };
        doWithRetry(actionName, supplier);
    }

    @Override
    public <T> T doWithRetry(String actionName, Supplier<T> function) {
        final String resolvedActionName = resolveActionName(actionName);
        final int retryCount = Math.max(0, retryConfiguration.getRetryCount());
        final int maxAttempts = retryCount + 1;
        int attempt = 1;

        while (attempt <= maxAttempts) {
            try {
                T result = function.get();
                LOGGER.info("Attempt {}/{} succeeded for action '{}'.", attempt, maxAttempts, resolvedActionName);
                return result;
            } catch (RuntimeException exception) {
                boolean handled = isExceptionHandled(handledExceptions, exception);
                boolean hasMoreAttempts = attempt < maxAttempts;

                if (!handled || !hasMoreAttempts) {
                    LOGGER.error("Attempt {}/{} failed for action '{}' and will not be retried! \n", attempt, maxAttempts, resolvedActionName);
                    throw exception;
                }

                long delayMs = retryConfiguration.getPollingInterval().toMillis();
                LOGGER.warn("Attempt {}/{} failed for action '{}' ({}: {}). Retrying after {} ms.",
                        attempt,
                        maxAttempts,
                        resolvedActionName,
                        exception.getClass().getSimpleName(),
                        exception.getMessage(),
                        delayMs);
                pauseBeforeRetry(delayMs);
                attempt++;
            }
        }

        // Should never be reached because the loop either returns or throws.
        throw new IllegalStateException("Unexpected retry loop termination for action: " + resolvedActionName);
    }

    protected boolean isExceptionHandled(Collection<Class<? extends Throwable>> handledExceptions, Throwable throwable) {
        return handledExceptions.stream().anyMatch(clazz -> clazz.isAssignableFrom(throwable.getClass()));
    }

    private String resolveActionName(String actionName) {
        return actionName == null || actionName.isBlank() ? DEFAULT_ACTION_NAME : actionName;
    }

    private void pauseBeforeRetry(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Retry pause interrupted for action retrier: {}", e.getMessage());
        }
    }
}
