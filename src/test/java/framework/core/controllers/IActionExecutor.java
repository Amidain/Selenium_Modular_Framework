package framework.core.controllers;

import java.util.function.Supplier;

/**
 * Coordinates execution of UI actions that require unified logging, retry logic, and failure diagnostics.
 */
public interface IActionExecutor {

    /**
     * Executes the provided action with retry semantics and emits structured logs before and after the attempt.
     *
     * @param elementName label describing the element involved in the action (e.g., "submit button", "email input"); used in logs and retry traces.
     * @param actionName label describing the action (e.g., "click", "type email"); used in logs and retry traces.
     * @param action supplier encapsulating the action to perform. The supplier must be idempotent because it can be invoked
     *        multiple times when retries are enabled.
    */
    <T> T performAction(String elementName, String actionName, Supplier<T> action);

    /**
     * Captures supplemental diagnostics (logs, screenshots, artifacts) for an action failure. Implementations decide
     * which artifacts to collect, but the method must never throw in order to avoid masking the original exception.
     *
     * @param elementName label describing the element involved in the action (e.g., "submit button", "email input"); used in logs and retry traces.
     * @param actionName label describing the action that triggered the failure; used for correlating artifacts with log statements.
     * @param error
     *        the exception raised by the action execution. Implementations should not swallow or modify this instance.
     */
    void captureFailureArtifact(String elementName, String actionName, RuntimeException error);
}
