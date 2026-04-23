package framework.elements.factory;

import framework.elements.base.BaseElement;
import framework.core.driver.DriverManager;
import org.openqa.selenium.WebDriver;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Carries configurable hooks for factory-created elements so dependencies can be injected centrally.
 * <p>Every element that flows through {@link ElementFactory} receives this context so we can
 * consistently supply the WebDriver instance and run cross-cutting initialisation (logging, AOP, etc.).</p>
 */
public final class ElementFactoryContext {

    private final Supplier<WebDriver> driverProvider;
    private final Consumer<BaseElement> elementInitializer;

    private ElementFactoryContext(Builder builder) {
        this.driverProvider = builder.driverProvider;
        this.elementInitializer = builder.elementInitializer;
    }

    /**
     * @return supplier used by elements to lazily access the WebDriver (defaults to {@link DriverManager}).
     */
    public Supplier<WebDriver> getDriverProvider() {
        return driverProvider;
    }

    /**
     * @return callback invoked after an element is created so common concerns (scrolling, logging hooks, etc.) run uniformly.
     */
    public Consumer<BaseElement> getElementInitializer() {
        return elementInitializer;
    }

    /**
     * @return opinionated context wiring DriverManager and a no-op initializer; safe default for most tests.
     */
    public static ElementFactoryContext defaultContext() {
        return builder().withDriverProvider(DriverManager::getWebDriver).withElementInitializer(element -> {}).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder used to declare how elements receive shared dependencies.
     */
    public static final class Builder {
        private Supplier<WebDriver> driverProvider = DriverManager::getWebDriver;
        private Consumer<BaseElement> elementInitializer = element -> {};

        private Builder() {
        }

        /**
         * Overrides the provider responsible for supplying WebDriver references to elements.
         */
        public Builder withDriverProvider(Supplier<WebDriver> driverProvider) {
            this.driverProvider = Objects.requireNonNull(driverProvider, "driverProvider");
            return this;
        }

        /**
         * Hooks a callback that executes immediately after every element is constructed.
         * Useful for injecting shared loggers, scroll helpers, or other cross-cutting wiring.
         */
        public Builder withElementInitializer(Consumer<BaseElement> elementInitializer) {
            this.elementInitializer = Objects.requireNonNull(elementInitializer, "elementInitializer");
            return this;
        }

        /**
         * Finalises the context definition.
         */
        public ElementFactoryContext build() {
            return new ElementFactoryContext(this);
        }
    }
}
