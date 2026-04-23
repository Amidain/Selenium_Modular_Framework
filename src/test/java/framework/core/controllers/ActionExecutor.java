package framework.core.controllers;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import framework.core.configs.Configuration;
import utils.ScreenShotMaker;

/**
 * Default implementation of {@link IActionExecutor} used by all component wrappers to keep retry logic and diagnostics
 * centralised in one place.
 */
public class ActionExecutor implements IActionExecutor {

    private final Logger logger;
    private final IActionRetrier actionRetrier;

    public ActionExecutor() {
        this(new ActionRetrier(Configuration.retryConfiguration()));
    }

    ActionExecutor(IActionRetrier actionRetrier) {
        this.logger = LogManager.getLogger(ActionExecutor.class);
        this.actionRetrier = Objects.requireNonNull(actionRetrier, "Action Retrier not provided in class constructor!");
    }

    @Override
    public <T> T performAction(String elementName, String actionName, Supplier<T> action) {
        Objects.requireNonNull(elementName, "<performAction> elementName not provided in method signature!");
        Objects.requireNonNull(actionName, "<performAction> actionName not provided in method signature!");
        Objects.requireNonNull(action, "<performAction> action not provided in method signature!");
        logger.info("Element action started: <{}> -> {}", elementName, actionName);
        try {
            T result = actionRetrier.doWithRetry(actionName, action);
            logger.info("Element action succeeded: <{}> -> {} \n", elementName, actionName);
            return result;
        } catch (RuntimeException error) {
            captureFailureArtifact(elementName, actionName, error);
            throw error;
        }
    }

    @Override
    public void captureFailureArtifact(String elementName, String actionName, RuntimeException error) {
        logger.error("Element action failed: {} -> {} | {}\n", elementName, actionName, error.getMessage(), error);
        if (!Configuration.reporting().isScreenshotOnFailureEnabled()) {
            return;
        }
        try {
            ScreenShotMaker.makeScreenShot(actionName, elementName);
        } catch (Exception screenshotError) {
            logger.warn("Failed to capture screenshot for {} -> {}: {}", elementName, actionName, screenshotError.getMessage());
        }
    }
}
