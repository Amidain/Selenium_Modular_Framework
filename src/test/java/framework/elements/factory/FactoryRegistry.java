package framework.elements.factory;

import framework.elements.base.BaseElement;
import org.openqa.selenium.By;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry that maps component classes to their builders.
 * <p>The registry is the canonical source of truth for how a locator + name are translated into
 * a fully initialised {@link BaseElement}. Tests or higher-level factories can swap registrations
 * to customise behaviour without touching page objects.</p>
 */
public class FactoryRegistry {

    @FunctionalInterface
    public interface ElementBuilder<T extends BaseElement> {
        /**
         * Creates a concrete element instance for the provided locator and display name.
         * The {@link ElementFactoryContext} travels along so builders can resolve shared dependencies.
         */
        T build(By locator, String name, ElementFactoryContext context);
    }

    private final Map<Class<? extends BaseElement>, ElementBuilder<? extends BaseElement>> builders = new ConcurrentHashMap<>();

    /**
     * Registers or replaces the builder for the specified element type.
     */
    public <T extends BaseElement> void register(Class<T> type, ElementBuilder<T> builder) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(builder, "builder");
        builders.put(type, builder);
    }

    /**
     * @return true when a builder has already been registered for the given type.
     */
    public <T extends BaseElement> boolean isRegistered(Class<T> type) {
        return builders.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    /**
     * Resolves the builder for an element type or throws when the registry does not know how to build it yet.
     */
    public <T extends BaseElement> ElementBuilder<T> get(Class<T> type) {
        Objects.requireNonNull(type, "type");
        ElementBuilder<T> builder = (ElementBuilder<T>) builders.get(type);
        if (builder == null) {
            throw new IllegalStateException("No builder registered for type: " + type.getName());
        }
        return builder;
    }

    /**
     * Removes the builder mapping for a type (useful for tests that override registrations temporarily).
     */
    public void unregister(Class<? extends BaseElement> type) {
        builders.remove(type);
    }

    /**
     * Clears every registration. Intended for cleanup inside tests.
     */
    public void clear() {
        builders.clear();
    }
}
